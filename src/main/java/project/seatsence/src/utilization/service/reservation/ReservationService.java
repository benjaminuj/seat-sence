package project.seatsence.src.utilization.service.reservation;

import static project.seatsence.global.code.ResponseCode.*;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.*;
import static project.seatsence.src.store.domain.ReservationUnit.*;
import static project.seatsence.src.utilization.domain.reservation.ReservationStatus.APPROVED;
import static project.seatsence.src.utilization.domain.reservation.ReservationStatus.PENDING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.store.domain.ReservationUnit;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.utilization.dao.reservation.ReservationRepository;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.reservation.ReservationStatus;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

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
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Reservation reservation) {
        // 겹치는 예약으로 처리 할 데이터의 예약 상태
        List<ReservationStatus> reservationStatuses = new ArrayList<>();
        reservationStatuses.add(ReservationStatus.APPROVED);
        reservationStatuses.add(ReservationStatus.PENDING);

        // 내가 예약하려는 시간과 겹치는 시간의 예약 데이터 조회
        List<Reservation> reservationsBasedStartDateTime =
                findAllByReservedStoreChairAndReservationStatusInAndStartScheduleIsBeforeAndEndScheduleIsAfterAndState(
                        storeChair, reservationStatuses, startDateTime, startDateTime);

        // 내가 예약하려는 시간을 기준으로, DB에 시간이 겹치는 이용건이 있을 경우
        if (reservationsBasedStartDateTime.size() > 0) {
            return -1;
        }

        // 내가 예약하려는 시간과 겹치는 시간의 예약 데이터 조회
        List<Reservation> reservationsBasedEndDateTime =
                findAllByReservedStoreChairAndReservationStatusInAndStartScheduleIsBeforeAndEndScheduleIsAfterAndState(
                        storeChair, reservationStatuses, endDateTime, endDateTime);

        // 내가 예약하려는 시간을 기준으로, DB에 시간이 겹치는 이용건이 있을 경우
        if (reservationsBasedEndDateTime.size() > 0) {
            return -1;
        }

        // 예약
        return save(reservation).getId();
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
