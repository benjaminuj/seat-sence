package project.seatsence.src.utilization.service.reservation;

import static project.seatsence.global.code.ResponseCode.SUCCESS_NO_CONTENT;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.*;
import static project.seatsence.src.utilization.domain.reservation.ReservationStatus.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.global.response.SliceResponse;
import project.seatsence.src.utilization.dao.reservation.ReservationRepository;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.reservation.ReservationStatus;
import project.seatsence.src.utilization.dto.response.reservation.AdminReservationListResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    public void reservationApprove(Long reservationId) {
        Reservation reservation = reservationService.findByIdAndState(reservationId);
        reservation.approveReservation();
    }

    public void reservationReject(Long reservationId) {
        Reservation reservation = reservationService.findByIdAndState(reservationId);
        reservation.rejectReservation();
    }

    public Slice<Reservation> getAllReservationAndState(Long storeId, Pageable pageable) {
        Slice<Reservation> reservationSlice =
                findAllByStoreIdAndStateOrderByStartScheduleDesc(storeId, pageable);
        if (!reservationSlice.hasContent()) {
            throw new BaseException(SUCCESS_NO_CONTENT);
        }
        return reservationSlice;
    }

    public Slice<Reservation> findAllByStoreIdAndStateOrderByStartScheduleDesc(
            Long storeId, Pageable pageable) {
        return reservationRepository.findAllByStoreIdAndStateOrderByStartScheduleDesc(
                storeId, ACTIVE, pageable);
    }

    public Slice<Reservation> getPendingReservation(Long storeId, Pageable pageable) {
        Slice<Reservation> reservationPendingSlice =
                findAllByStoreIdAndReservationStatusAndStateOrderByStartScheduleAsc(
                        storeId, PENDING, pageable);
        if (!reservationPendingSlice.hasContent()) {
            throw new BaseException(SUCCESS_NO_CONTENT);
        }
        return reservationPendingSlice;
    }

    public Slice<Reservation> findAllByStoreIdAndReservationStatusAndStateOrderByStartScheduleAsc(
            Long storeId, ReservationStatus reservationStatus, Pageable pageable) {
        return reservationRepository
                .findAllByStoreIdAndReservationStatusAndStateOrderByStartScheduleAsc(
                        storeId, reservationStatus, ACTIVE, pageable);
    }

    public Slice<Reservation> getProcessedReservation(Long storeId, Pageable pageable) {
        Slice<Reservation> reservationProcessedSlice =
                findAllByStoreIdAndReservationStatusNotAndStateOrderByUpdatedAtDesc(
                        storeId, PENDING, pageable);

        if (!reservationProcessedSlice.hasContent()) {
            throw new BaseException(SUCCESS_NO_CONTENT);
        }
        return reservationProcessedSlice;
    }

    public Slice<Reservation> findAllByStoreIdAndReservationStatusNotAndStateOrderByUpdatedAtDesc(
            Long storeId, ReservationStatus reservationStatus, Pageable pageable) {
        return reservationRepository
                .findAllByStoreIdAndReservationStatusNotAndStateOrderByUpdatedAtDesc(
                        storeId, reservationStatus, ACTIVE, pageable);
    }

    public SliceResponse<AdminReservationListResponse.ReservationResponse> toSliceResponse(
            Slice<Reservation> reservationSlice) {
        return SliceResponse.of(reservationSlice.map(this::toReservationResponse));
    }

    private AdminReservationListResponse.ReservationResponse toReservationResponse(
            Reservation reservation) {
        String reservationUnitReservedByUser = null;
        String reservedPlace = null;
        String storeSpaceName = null;

        if (reservation.getReservedStoreSpace() != null) {
            reservationUnitReservedByUser = "스페이스";
            reservedPlace = reservation.getReservedStoreSpace().getName();
            storeSpaceName = reservation.getReservedStoreSpace().getName();
        } else {
            reservationUnitReservedByUser = "좌석";
            reservedPlace = String.valueOf(reservation.getReservedStoreChair().getManageId());
            storeSpaceName = reservation.getReservedStoreChair().getStoreSpace().getName();
        }

        return AdminReservationListResponse.ReservationResponse.builder()
                .id(reservation.getId())
                .name(reservation.getUser().getName())
                .reservationStatus(reservation.getReservationStatus())
                .storeSpaceName(storeSpaceName)
                .reservationUnitReservedByUser(reservationUnitReservedByUser)
                .reservedPlace(reservedPlace)
                .startSchedule(reservation.getStartSchedule())
                .endSchedule(reservation.getEndSchedule())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
