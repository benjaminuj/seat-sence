package project.seatsence.src.utilization.dto.request;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUtilizationContentRequest {

    @NotNull private Long fieldId;

    @NotEmpty(message = "이용 신청 정보 내용이 입력되지 않았습니다.")
    private List<String> content;
}
