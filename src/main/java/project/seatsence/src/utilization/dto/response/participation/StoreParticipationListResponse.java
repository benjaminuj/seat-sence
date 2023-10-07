package project.seatsence.src.utilization.dto.response.participation;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StoreParticipationListResponse {

    private List<SpaceResponse> spaceResponseList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class SpaceResponse {
        private Long id;
        private String utilizationUnit; // 예약 또는 바로 사용
        private String storeSpaceName;
        private LocalDateTime startSchedule;
        private LocalDateTime endSchedule;
        private String userNickname;
    }
}
