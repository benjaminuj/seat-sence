package project.seatsence.src.utilization.service.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import project.seatsence.global.code.ResponseCode;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.utilization.dto.request.ChairUtilizationRequest;
import project.seatsence.src.utilization.dto.request.CustomUtilizationContentRequest;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class UserReservationServiceTest {
    @Autowired private UserReservationService userReservationService;

    // 의자 예약 동시성 검증 코드
    @Test
    void testChairReservationConcurrency() throws InterruptedException {
        // Given
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        String userEmail = "yes@naver.com";
        long storeChairId = 1500031;

        LocalDateTime startSchedule1 = LocalDateTime.of(2024, 10, 29, 3, 0, 0);
        LocalDateTime endSchedule1 = LocalDateTime.of(2024, 10, 29, 6, 30, 0);

        LocalDateTime startSchedule2 = LocalDateTime.of(2024, 10, 29, 4, 0, 0);
        LocalDateTime endSchedule2 = LocalDateTime.of(2024, 10, 29, 7, 30, 0);

        long fieldId = 1;
        List<String> content = Arrays.asList("스터디");
        CustomUtilizationContentRequest customUtilizationContentRequest =
                new CustomUtilizationContentRequest(fieldId, content);

        ChairUtilizationRequest utilizationRequest1 =
                new ChairUtilizationRequest(
                        storeChairId,
                        startSchedule1,
                        endSchedule1,
                        Arrays.asList(customUtilizationContentRequest));
        ChairUtilizationRequest utilizationRequest2 =
                new ChairUtilizationRequest(
                        storeChairId,
                        startSchedule2,
                        endSchedule2,
                        Arrays.asList(customUtilizationContentRequest));

        // When
        List<Long> ids = new ArrayList<>();

        service.execute(
                () -> {
                    long id = -1;

                    try {
                        id =
                                userReservationService.chairReservation(
                                        userEmail, utilizationRequest1);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (BaseException baseException) {
                        log.error(ResponseCode.RESERVATION_ALREADY_EXIST.getMessage());
                    }

                    if (id != -1) {
                        synchronized (this) {
                            ids.add(id);
                        }
                    }
                    latch.countDown();
                });

        service.execute(
                () -> {
                    long id = -1;
                    try {
                        id =
                                userReservationService.chairReservation(
                                        userEmail, utilizationRequest2);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (BaseException baseException) {
                        log.error(ResponseCode.RESERVATION_ALREADY_EXIST.getMessage());
                    }

                    if (id != -1) {
                        synchronized (this) {
                            ids.add(id);
                        }
                    }
                    latch.countDown();
                });
        latch.await();

        // Then
        Assertions.assertThat(ids.size()).isEqualTo(1);
    }
}
