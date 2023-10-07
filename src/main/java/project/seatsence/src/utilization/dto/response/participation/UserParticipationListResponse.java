package project.seatsence.src.utilization.dto.response.participation;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserParticipationListResponse {
    private List<ParticipationResponse> ParticipationResponseList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ParticipationResponse {
        private Long id;
        private String storeName;
        private String storeSpaceName;
        private LocalDateTime startSchedule;
        private LocalDateTime endSchedule;
        private LocalDateTime createdAt;
        private String storeMainImage;
        private String userNickname;
    }
}
