package project.seatsence.src.utilization.service.reservation;

import static project.seatsence.global.code.ResponseCode.LOCK_INTERRUPTED_ERROR;
import static project.seatsence.global.code.ResponseCode.LOCK_NOT_AVAILABLE;
import static project.seatsence.global.code.ResponseCode.UNLOCKING_A_LOCK_WHICH_IS_NOT_LOCKED;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.utilization.domain.reservation.Reservation;

@Component
@RequiredArgsConstructor
@Slf4j
public class NamedLockUserReservationFacade {
    private final ReservationService reservationService;
    private final RedissonClient redissonClient;

    @Transactional
    public long chairReservation(
            StoreChair storeChair,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Reservation reservation) {
        long chairId = -1;

        String lockName = "CHAIR" + storeChair.getId();
        RLock rLock = redissonClient.getLock(lockName);

        long waitTime = 5L;
        long leaseTime = 3L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        try {
            // 분산 락 획득
            boolean available = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!available) {
                throw new BaseException(LOCK_NOT_AVAILABLE);
            }
            log.info("acquire lock: " + redissonClient.getLock(lockName).getName());

            // 예약 가능한지 확인 및 가능한 경우 예약
            chairId =
                    reservationService.checkExistsReservationDateTimeAndSave(
                            storeChair, startDateTime, endDateTime, reservation);

        } catch (InterruptedException e) {
            // 락을 얻으려고 시도하다가 인터럽트를 받았을 때 예외
            throw new BaseException(LOCK_INTERRUPTED_ERROR);
        } finally {
            try {
                // 락 해제
                rLock.unlock();
                log.info("unlock complete: {}", rLock.getName());
            } catch (IllegalMonitorStateException e) {
                // 이미 종료된 락일 때 발생하는 예외
                throw new BaseException(UNLOCKING_A_LOCK_WHICH_IS_NOT_LOCKED);
            }
        }

        return chairId;
    }
}
