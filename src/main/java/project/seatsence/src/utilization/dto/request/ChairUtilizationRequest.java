package project.seatsence.src.utilization.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChairUtilizationRequest {
    @NotNull(message = "의자 식별자가 입력되지 않았습니다.")
    @Parameter(
            description = "이용하려는 가게 의자의 id (manageId 아님/ idByWeb 아님)",
            required = true,
            example = "1")
    private Long storeChairId;

    @NotNull(message = "이용 시작 일정이 입력되지 않았습니다.")
    @Parameter(description = "이용 시작 날짜와 시간", required = true, example = "2023-08-07T10:30:00.000")
    private LocalDateTime startSchedule;

    @NotNull(message = "이용 끝 일정이 입력되지 않았습니다.")
    @Parameter(description = "이용 끝 날짜와 시간", required = true, example = "2023-08-07T11:30:00.000")
    private LocalDateTime endSchedule;

    private List<CustomUtilizationContentRequest> customUtilizationContents;
}
