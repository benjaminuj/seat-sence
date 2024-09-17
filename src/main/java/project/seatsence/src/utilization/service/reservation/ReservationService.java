package project.seatsence.src.utilization.service.reservation;

import static project.seatsence.global.code.ResponseCode.*;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.*;
import static project.seatsence.src.store.domain.ReservationUnit.*;
import static project.seatsence.src.utilization.domain.reservation.ReservationStatus.APPROVED;
import static project.seatsence.src.utilization.domain.reservation.ReservationStatus.PENDING;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.store.domain.CustomUtilizationField;
import project.seatsence.src.store.domain.ReservationUnit;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.store.service.StoreChairService;
import project.seatsence.src.store.service.StoreCustomService;
import project.seatsence.src.store.service.StoreService;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.user.service.UserService;
import project.seatsence.src.utilization.dao.CustomUtilizationContentRepository;
import project.seatsence.src.utilization.dao.reservation.ReservationRepository;
import project.seatsence.src.utilization.domain.CustomUtilizationContent;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.reservation.ReservationStatus;
import project.seatsence.src.utilization.dto.request.ChairUtilizationRequest;
import project.seatsence.src.utilization.dto.request.CustomUtilizationContentRequest;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreCustomService storeCustomService;
    private final CustomUtilizationContentRepository customUtilizationContentRepository;

    // test
    private final StoreChairService storeChairService;
    private final UserService userService;
    private final StoreService storeService;

    public Reservation findByIdAndState(Long id) {
        return reservationRepository
                .findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(RESERVATION_NOT_FOUND));
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Boolean isPossibleTimeToManageReservationStatus(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(reservation.getEndSchedule());
    }

    public Boolean isPossibleReservationStatusToCancel(Reservation reservation) {
        Boolean isPossible = false;
        if ((reservation.getReservationStatus().equals(PENDING))
                || (reservation.getReservationStatus().equals(APPROVED))) {
            isPossible = true;
        }
        return isPossible;
    }

    public void checkValidationToModifyReservationStatus(Reservation reservation) {
        if (!isPossibleReservationStatusToCancel(reservation)) {
            throw new BaseException(INVALID_RESERVATION_STATUS);
        }

        if (!isPossibleTimeToManageReservationStatus(reservation)) {
            throw new BaseException(INVALID_TIME_TO_MODIFY_RESERVATION_STATUS);
        }
    }

    public ReservationUnit getUtilizationUnitOfReservation(Reservation reservation) {
        ReservationUnit utilizationUnit = null;
        if (reservation.getReservedStoreSpace() != null) {
            utilizationUnit = SPACE;
        } else if (reservation.getReservedStoreChair() != null) {
            utilizationUnit = CHAIR;
        }
        return utilizationUnit;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long checkExistsReservationDateTimeAndSave(
            StoreChair storeChair,
            Reservation reservation,
            ChairUtilizationRequest chairUtilizationRequest,
            String userEmail,
            User user)
            throws JsonProcessingException {
        List<ReservationStatus> reservationStatuses = new ArrayList<>();
        reservationStatuses.add(ReservationStatus.APPROVED);
        reservationStatuses.add(ReservationStatus.PENDING);
        System.out.println("겹치는 예약 데이터 조회 전 : " + Thread.currentThread().getName());
        List<Reservation> reservationsBasedStartDateTime =
                findAllByReservedStoreChairAndReservationStatusInAndStartScheduleIsBeforeAndEndScheduleIsAfterAndState(
                        storeChair,
                        reservationStatuses,
                        chairUtilizationRequest.getStartSchedule(),
                        chairUtilizationRequest.getStartSchedule());
        System.out.println("겹치는 예약 데이터 조회 후 : " + Thread.currentThread().getName());
        if (reservationsBasedStartDateTime.size() > 0) {
            System.out.println("예약 시간 겹침 : " + Thread.currentThread().getName());
            return -1;
        }

        List<Reservation> reservationsBasedEndDateTime =
                findAllByReservedStoreChairAndReservationStatusInAndStartScheduleIsBeforeAndEndScheduleIsAfterAndState(
                        storeChair,
                        reservationStatuses,
                        chairUtilizationRequest.getEndSchedule(),
                        chairUtilizationRequest.getEndSchedule());

        if (reservationsBasedEndDateTime.size() > 0) {
            System.out.println("예약 시간 겹침 : " + Thread.currentThread().getName());
            return -1;
        }

        System.out.println("예약 가능 : " + Thread.currentThread().getName());

        //        StoreChair storeChair1 =
        //                storeChairService.findByIdAndState(1L);
        //        User user1 = userService.findByEmailAndState(userEmail);
        //        Store store1 = storeService.findByIdAndState(1L);
        //
        //
        //        Reservation reservation1 =
        //                Reservation.builder()
        //                        .store(store1)
        //                        .reservedStoreChair(storeChair1)
        //                        .reservedStoreSpace(null)
        //                        .user(user1)
        //                        .startSchedule(startDateTime)
        //                        .endSchedule(endDateTime)
        //                        .build();

        long reservationId = save(reservation).getId();
        inputChairCustomUtilizationContent(user, reservation, chairUtilizationRequest);
        return reservationId;
    }

    public void inputChairCustomUtilizationContent(
            User user, Reservation reservation, ChairUtilizationRequest chairUtilizationRequest)
            throws JsonProcessingException {

        for (CustomUtilizationContentRequest request :
                chairUtilizationRequest.getCustomUtilizationContents()) {

            CustomUtilizationField customUtilizationField =
                    storeCustomService.findByIdAndState(request.getFieldId());

            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(request.getContent());

            CustomUtilizationContent newCustomUtilizationContent =
                    new CustomUtilizationContent(
                            user, customUtilizationField, reservation, null, content);
            customUtilizationContentRepository.save(newCustomUtilizationContent);
        }
    }

    public List<Reservation>
            findAllByReservedStoreChairAndReservationStatusInAndStartScheduleIsBeforeAndEndScheduleIsAfterAndState(
                    StoreChair storeChair,
                    List<ReservationStatus> reservationStatuses,
                    LocalDateTime reservationDateTime1,
                    LocalDateTime reservationDateTime2) {
        return reservationRepository
                .findAllByReservedStoreChairAndReservationStatusInAndStartScheduleIsBeforeAndEndScheduleIsAfterAndState(
                        storeChair,
                        reservationStatuses,
                        reservationDateTime1,
                        reservationDateTime2,
                        ACTIVE);
    }
}
