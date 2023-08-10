package project.seatsence.src.utilization.dto.reservation.response;

import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import project.seatsence.src.utilization.domain.reservation.ReservationStatus;

@Getter
@AllArgsConstructor
@Builder
public class ReservationListResponse {
    private List<ReservationResponse> reservationResponseList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ReservationResponse {
        private Long id;
        private String name;
        private ReservationStatus reservationStatus;
        @Nullable private Long storeSpaceId;
        @Nullable private Long storeChairId;
        private LocalDateTime reservationStartDateAndTime;
        private LocalDateTime reservationEndDateAndTime;
        private LocalDateTime createdAt;
    }
}