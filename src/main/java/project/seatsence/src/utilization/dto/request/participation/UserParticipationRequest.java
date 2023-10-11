package project.seatsence.src.utilization.dto.request.participation;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserParticipationRequest {

    @NotNull(message = "참여 신청할 id를 입력해주세요.")
    private Long id;

    @NotNull(message = "예약 또는 바로 사용을 입력해주세요.")
    private String utilizationUnit; // 예약 또는 바로 사용
}
