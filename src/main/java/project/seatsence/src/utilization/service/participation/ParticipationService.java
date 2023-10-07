package project.seatsence.src.utilization.service.participation;

import static project.seatsence.global.code.ResponseCode.PARTICIPATION_NOT_FOUND;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.ACTIVE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.global.response.SliceResponse;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.service.StoreService;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.user.service.UserService;
import project.seatsence.src.utilization.dao.participation.ParticipationRepository;
import project.seatsence.src.utilization.domain.Participation.Participation;
import project.seatsence.src.utilization.domain.Participation.ParticipationStatus;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.walkin.WalkIn;
import project.seatsence.src.utilization.dto.response.participation.StoreParticipationListResponse;
import project.seatsence.src.utilization.dto.response.participation.UserParticipationListResponse;
import project.seatsence.src.utilization.service.reservation.ReservationService;
import project.seatsence.src.utilization.service.reservation.UserReservationService;
import project.seatsence.src.utilization.service.walkin.UserWalkInService;
import project.seatsence.src.utilization.service.walkin.WalkInService;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final UserService userService;

    private final ParticipationRepository participationRepository;

    private final ReservationService reservationService;

    private final WalkInService walkInService;
    private final StoreService storeService;

    private final UserReservationService userReservationService;

    private final UserWalkInService userWalkInService;

    public Participation findByIdAndState(Long id) {
        return participationRepository
                .findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(PARTICIPATION_NOT_FOUND));
    }

    public Slice<Participation> getUpcomingParticipation(String userEmail, Pageable pageable) {
        User user = userService.findUserByUserEmailAndState(userEmail);

        Slice<Participation> participationSlice =
                findAllByUserEmailAndParticipationStatusAndStateOrderByStartScheduleDesc(
                        userEmail, ParticipationStatus.UPCOMING_PARTICIPATION, pageable);

        return participationSlice;
    }

    Slice<Participation> findAllByUserEmailAndParticipationStatusAndStateOrderByStartScheduleDesc(
            String email, ParticipationStatus participationStatus, Pageable pageable) {
        return participationRepository
                .findAllByUserEmailAndParticipationStatusAndStateOrderByStartScheduleDesc(
                        email, participationStatus, ACTIVE, pageable);
    }

    public Slice<Participation> getParticipated(String userEmail, Pageable pageable) {
        User user = userService.findUserByUserEmailAndState(userEmail);

        Slice<Participation> participationSlice =
                findAllByUserEmailAndParticipationStatusAndStateOrderByStartScheduleDesc(
                        userEmail, ParticipationStatus.PARTICIPATED, pageable);

        return participationSlice;
    }

    public SliceResponse<UserParticipationListResponse.ParticipationResponse> getSpace(
            Slice<Participation> participationSlice) {
        return SliceResponse.of(participationSlice.map(this::toParticipationResponse));
    }

    private UserParticipationListResponse.ParticipationResponse toParticipationResponse(
            Participation participation) {

        String participationPlace = null;
        String storeSpaceName = null;
        LocalDateTime endSchedule = null;

        if (participation.getReservation() != null) { // 예약
            Reservation reservation =
                    reservationService.findByIdAndState(participation.getReservation().getId());
            storeSpaceName = reservation.getReservedStoreSpace().getName();
            endSchedule = reservation.getEndSchedule();
            System.out.println("예약");
        } else { // 바로사용
            WalkIn walkIn = walkInService.findByIdAndState(participation.getWalkIn().getId());
            storeSpaceName = walkIn.getUsedStoreSpace().getName();
            endSchedule = walkIn.getEndSchedule();
            System.out.println("바로사용");
        }

        return UserParticipationListResponse.ParticipationResponse.builder()
                .id(participation.getId())
                .storeName(participation.getStore().getStoreName())
                .storeSpaceName(storeSpaceName)
                .startSchedule(participation.getStartSchedule())
                .endSchedule(endSchedule)
                .createdAt(participation.getCreatedAt())
                .storeMainImage(storeService.getStoreMainImage(participation.getStore().getId()))
                .userNickname(participation.getUser().getNickname())
                .build();
    }

    public void cancelParticipation(Long participationId) {
        Participation participation = findByIdAndState(participationId);
        participation.cancelParticipation();
    }

    public SliceResponse<StoreParticipationListResponse.SpaceResponse> getParticipationSpace(
            Long storeId, Pageable pageable) {

        Store store = storeService.findByIdAndState(storeId);
        List<WalkIn> walkIns =
                userWalkInService
                        .findByStoreIdAndEndScheduleAfterAndUsedStoreSpaceIdIsNotNullAndState(
                                storeId);
        List<Reservation> reservations =
                userReservationService
                        .findByStoreIdAndReservationStatusAndEndScheduleAfterAndReservedStoreSpaceIdIsNotNullAndState(
                                storeId);

        List<StoreParticipationListResponse.SpaceResponse> combinedList = new ArrayList<>();

        for (WalkIn walkIn : walkIns) {
            combinedList.add(toSpaceResponse(null, walkIn));
        }
        for (Reservation reservation : reservations) {
            combinedList.add(toSpaceResponse(reservation, null));
        }

        combinedList.sort(
                Comparator.comparing(
                        StoreParticipationListResponse.SpaceResponse::getStartSchedule));

        /** 페이지네이션 처리 */
        int start = pageable.getPageNumber() * pageable.getPageSize();

        if (start >= combinedList.size()) {
            return SliceResponse.of(
                    Collections.emptyList(), (long) pageable.getPageNumber(), 0, false);
        }

        int end = Math.min(start + pageable.getPageSize(), combinedList.size());
        List<StoreParticipationListResponse.SpaceResponse> content =
                combinedList.subList(start, end);
        boolean hasNext = end < combinedList.size();

        return SliceResponse.of(content, (long) pageable.getPageNumber(), content.size(), hasNext);
    }

    private StoreParticipationListResponse.SpaceResponse toSpaceResponse(
            Reservation reservation, WalkIn walkIn) {

        String utilizationUnit = null;
        Long id = null;
        String storeSpaceName = null;
        LocalDateTime startSchedule = null;
        LocalDateTime endSchedule = null;
        String userNickname = null;

        if (reservation != null) { // 예약
            utilizationUnit = "예약";
            id = reservation.getId();
            storeSpaceName = reservation.getReservedStoreSpace().getName();
            startSchedule = reservation.getStartSchedule();
            endSchedule = reservation.getEndSchedule();
            userNickname = reservation.getUser().getNickname();
        } else { // 바로사용
            utilizationUnit = "바로 사용";
            id = walkIn.getId();
            storeSpaceName = walkIn.getUsedStoreSpace().getName();
            startSchedule = walkIn.getStartSchedule();
            endSchedule = walkIn.getEndSchedule();
            userNickname = walkIn.getUser().getNickname();
        }

        return StoreParticipationListResponse.SpaceResponse.builder()
                .id(id)
                .utilizationUnit(utilizationUnit)
                .storeSpaceName(storeSpaceName)
                .startSchedule(startSchedule)
                .endSchedule(endSchedule)
                .userNickname(userNickname)
                .build();
    }
}
