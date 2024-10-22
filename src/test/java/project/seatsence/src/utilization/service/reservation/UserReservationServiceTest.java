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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
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
    //    @Sql(scripts = "/sql/delete-all-test-data.sql", executionPhase =
    // ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/user-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/store-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(
            scripts = "/sql/store-space-test-data.sql",
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(
            scripts = "/sql/store-chair-test-data.sql",
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(
            scripts = "/sql/store-table-test-data.sql",
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(
            scripts = "/sql/custom-utilization-field-test-data.sql",
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(
            scripts = "/sql/delete-all-test-data.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testChairReservationConcurrency() throws InterruptedException {
        // Given
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 예약자 2명
        String user1Email = "gildong@naver.com";
        String user2Email = "minji@naver.com";
        // 예약 의자 id (겹침)
        long storeChairId = 1L;

        // 예약 시간1
        LocalDateTime startSchedule1 = LocalDateTime.of(2024, 10, 29, 3, 0, 0);
        LocalDateTime endSchedule1 = LocalDateTime.of(2024, 10, 29, 6, 30, 0);

        // 예약 시간2
        LocalDateTime startSchedule2 = LocalDateTime.of(2024, 10, 29, 4, 0, 0);
        LocalDateTime endSchedule2 = LocalDateTime.of(2024, 10, 29, 7, 30, 0);

        // 예약시 작성해야하는 컨텐츠 필드 id
        long fieldId = 1;
        // 예약시 작성해야하는 컨텐츠 필드의 내용
        List<String> content = Arrays.asList("스터디");
        // 예약시 작성하는 컨텐츠 요청 객체
        CustomUtilizationContentRequest customUtilizationContentRequest =
                new CustomUtilizationContentRequest(fieldId, content);

        // 예약 요청 객체 1
        ChairUtilizationRequest utilizationRequest1 =
                new ChairUtilizationRequest(
                        storeChairId,
                        startSchedule1,
                        endSchedule1,
                        Arrays.asList(customUtilizationContentRequest));

        // 예약 요청 객체 2
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
                                        user1Email, utilizationRequest1);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (BaseException baseException) {
                        log.error(baseException.getResponseCode().getMessage());
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
                                        user2Email, utilizationRequest2);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (BaseException baseException) {
                        log.error(baseException.getResponseCode().getMessage());
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
