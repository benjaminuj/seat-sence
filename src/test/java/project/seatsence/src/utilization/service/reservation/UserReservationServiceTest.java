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
import project.seatsence.src.utilization.dto.request.ChairUtilizationRequest;
import project.seatsence.src.utilization.dto.request.CustomUtilizationContentRequest;

@Slf4j
@SpringBootTest
class UserReservationServiceTest {

    @Autowired private UserReservationService userReservationService;

    @Test
    void testChairReservationConcurrency() throws InterruptedException {
        System.out.println("chairReservation 동시성 테스트 시작");

        // Given
        System.out.println("chairReservation 동시성 테스트 준비");
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        String userEmail = "yes@naver.com";
        long storeChairId = 1500031;
        LocalDateTime startSchedule = LocalDateTime.of(2024, 10, 30, 3, 0, 0);
        LocalDateTime endSchedule = LocalDateTime.of(2024, 10, 30, 3, 30, 0);

        long fieldId = 1;
        List<String> content = Arrays.asList("스터디");
        CustomUtilizationContentRequest customUtilizationContentRequest =
                new CustomUtilizationContentRequest(fieldId, content);

        ChairUtilizationRequest utilizationRequest1 =
                new ChairUtilizationRequest(
                        storeChairId,
                        startSchedule,
                        endSchedule,
                        Arrays.asList(customUtilizationContentRequest));
        ChairUtilizationRequest utilizationRequest2 =
                new ChairUtilizationRequest(
                        storeChairId,
                        startSchedule,
                        endSchedule,
                        Arrays.asList(customUtilizationContentRequest));

        // When
        System.out.println("chairReservation 동시성 테스트 진행");
        List<Long> ids = new ArrayList<>();

        Object object = new Object();

        service.execute(
                () -> {
                    try {
                        long id =
                                userReservationService.chairReservation(
                                        userEmail, utilizationRequest1);

                        if (id != -1) {
                            synchronized (object) {
                                ids.add(id);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    latch.countDown();
                });
        service.execute(
                () -> {
                    try {
                        long id =
                                userReservationService.chairReservation(
                                        userEmail, utilizationRequest2);

                        if (id != -1) {
                            synchronized (object) {
                                ids.add(id);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    latch.countDown();
                });
        latch.await();

        // Then
        System.out.println("makeReservation 동시성 테스트 결과 검증");
        Assertions.assertThat(ids.size()).isEqualTo(1);
    }
}
