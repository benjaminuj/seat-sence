package project.seatsence.src.utilization.service.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDateTime;
import jdk.jfr.StackTrace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.code.ResponseCode;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.LockRepository;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.dto.request.ChairUtilizationRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class NamedLockUserReservationFacade {
    private final LockRepository lockRepository;
    private final ReservationService reservationService;

    @Transactional
    public long chairReservation(
            StoreChair storeChair,
            Reservation reservation,
            ChairUtilizationRequest chairUtilizationRequest,
            String userEmail,
            User user) throws JsonProcessingException {
        long chairId = -1;

        try {
//            lockRepository.getLock(storeChair.getId().toString());

            Integer lock = lockRepository.getLock(storeChair.getId().toString());
            if (lock == 1) {
                System.out.println("Lock acquired by: " + Thread.currentThread().getName());
            } else if (lock == 0){
                System.out.println("Failed to acquire lock during timeout: " + Thread.currentThread().getName());
            } else if (lock == null) {
                System.out.println("Failed to acquire lock error!: " + Thread.currentThread().getName());
            }

            chairId =
                    reservationService.checkExistsReservationDateTimeAndSave(
                            storeChair, reservation, chairUtilizationRequest, userEmail, user);

            if (chairId == -1) {
                System.out.println("facade: 이미 예약이 존재!!!");
                throw new BaseException(ResponseCode.RESERVATION_ALREADY_EXIST);
            } else {
                System.out.println("예약 성공! : " + Thread.currentThread().getName());
            }
        } catch (Exception e) {
            System.out.println("exeption 발생!!! : " + Thread.currentThread().getName());
            e.printStackTrace();

        } finally {

            System.out.println("finally!! " + Thread.currentThread().getName());
            lockRepository.releaseLock(storeChair.getId().toString());

            return chairId;
        }
    }
}
