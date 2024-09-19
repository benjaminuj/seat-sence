package project.seatsence.src.utilization.service.reservation;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

        // named lock 획득
        Integer lock = lockRepository.getLock(storeChair.getId().toString());
        if (lock == 1) {
            System.out.println("Lock acquired by: " + Thread.currentThread().getName());
        } else if (lock == 0) {
            System.out.println(
                    "Failed to acquire lock during timeout: " + Thread.currentThread().getName());
        } else if (lock == null) {
            System.out.println(
                    "Failed to acquire lock error!: " + Thread.currentThread().getName());
        }

        // 예약 가능한지 확인 및 가능한 경우 예약
        chairId =
                reservationService.checkExistsReservationDateTimeAndSave(
                        storeChair, startDateTime, endDateTime, reservation);

        // named lock 해제
        lockRepository.releaseLock(storeChair.getId().toString());

        return chairId;
    }
}
