package project.seatsence.src.utilization.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.seatsence.src.utilization.dto.response.AllUtilizationsForSeatAndDateResponse;
import project.seatsence.src.utilization.service.UserUtilizationService;

@RequestMapping("/v1/utilization")
@RestController
@RequiredArgsConstructor
@Tag(name = "10. [Utilization - User]")
@Slf4j
@Validated
public class UserUtilizationApi {
    private final UserUtilizationService userUtilizationService;

    @Operation(
            summary = "특정 스페이스와 날짜의 바로 사용 중이거나 예약된 시간 조회",
            description = "선택한 스페이스와 날짜에 예약(대기or승인) 및 바로사용 되어있는 모든 시간을 조회합니다.")
    @GetMapping("/valid-list/space/date/{space-id}")
    public AllUtilizationsForSeatAndDateResponse getAllUtilizationsForSpaceAndDate(
            @Parameter(
                            description = "이용하려는 스페이스의 식별자",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "10")
                    @PathVariable("space-id")
                    Long spaceId,
            @Parameter(
                            description =
                                    "이용(예약 or 바로 사용)하려는 시간과 날짜 / 당일이면 시간은 현재시간으로, 다른 날이면 해당 날의 00시00분00초로 요청",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "2023-08-07T10:30:00.000")
                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                    @RequestParam("standardSchedule")
                    LocalDateTime standardSchedule) {

        List<AllUtilizationsForSeatAndDateResponse.UtilizationForSeatAndDate>
                mappedUtilizationsForSpace =
                        userUtilizationService.getAllUtilizationsForSpaceAndDate(
                                spaceId, standardSchedule);

        AllUtilizationsForSeatAndDateResponse response =
                new AllUtilizationsForSeatAndDateResponse(mappedUtilizationsForSpace);

        return response;
    }

    @Operation(
            summary = "특정 의자와 날짜의 바로 사용 중이거나 예약된 시간 조회",
            description = "선택한 의자와 날짜에 예약(대기or승인) 및 바로사용 되어있는 모든 시간을 조회합니다.")
    @GetMapping("/valid-list/chair/date/{chair-id}")
    public AllUtilizationsForSeatAndDateResponse getAllUtilizationsForChairAndDate(
            @Parameter(
                            description = "이용하려는 의자의 식별자",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "10")
                    @PathVariable("chair-id")
                    Long chairId,
            @Parameter(
                            description =
                                    "이용(예약 or 바로 사용)하려는 시간과 날짜 / 당일이면 시간은 현재시간으로, 다른 날이면 해당 날의 00시00분00초로 요청",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "2023-08-07T10:30:00.000")
                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                    @RequestParam("standardSchedule")
                    LocalDateTime standardSchedule) {
        List<AllUtilizationsForSeatAndDateResponse.UtilizationForSeatAndDate> mappedReservations =
                userUtilizationService.getAllUtilizationsForChairAndDate(chairId, standardSchedule);

        AllUtilizationsForSeatAndDateResponse response =
                new AllUtilizationsForSeatAndDateResponse(mappedReservations);

        return response;
    }
}
