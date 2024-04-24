package project.seatsence.src.utilization.service.reservation;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.code.ResponseCode;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.LockRepository;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.utilization.domain.reservation.Reservation;

@Component
@RequiredArgsConstructor
@Slf4j
public class NamedLockUserReservationFacade {
    private final LockRepository lockRepository;
    private final ReservationService reservationService;

    @Transactional
    public long chairReservation(
            StoreChair storeChair,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Reservation reservation) {
        long chairId = -1;

        try {
            lockRepository.getLock(storeChair.getId().toString());
            chairId =
                    reservationService.checkExistsReservationDateTimeAndSave(
                            storeChair, startDateTime, endDateTime, reservation);

            if (chairId == -1) {
                throw new BaseException(ResponseCode.RESERVATION_ALREADY_EXIST);
            }
        } finally {
            lockRepository.releaseLock(storeChair.getId().toString());

            return chairId;
        }
    }
}
