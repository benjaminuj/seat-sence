package project.seatsence.src.utilization.api;

import static project.seatsence.src.store.domain.ReservationUnit.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.seatsence.src.utilization.domain.Utilization;
import project.seatsence.src.utilization.dto.response.LoadSeatsCurrentlyInUseResponse;
import project.seatsence.src.utilization.service.UtilizationService;

@RequestMapping("/v1/utilization")
@RestController
@RequiredArgsConstructor
@Tag(name = "11. [Utilization]")
@Slf4j
@Validated
public class UtilizationApi {
    private final UtilizationService utilizationService;

    @Operation(summary = "현재 이용중인 좌석 조회", description = "현재 이용중인 좌석(의자, 스페이스)을 조회합니다")
    @GetMapping("/seat/current-in-use/{space-id}")
    public LoadSeatsCurrentlyInUseResponse loadSeatCurrentlyInUse(
            @Parameter(description = "스페이스 식별자", in = ParameterIn.PATH, example = "1")
                    @PathVariable("space-id")
                    Long spaceId) {
        List<Utilization> utilizationBySpace =
                utilizationService.findSeatCurrentlyInUseByUnit(spaceId, SPACE);

        if (utilizationBySpace.size() != 0) {
            return LoadSeatsCurrentlyInUseResponse.builder()
                    .isThisSpaceCurrentlyInUse(true)
                    .allChairsCurrentlyInUse(new ArrayList<>())
                    .build();
        }

        List<LoadSeatsCurrentlyInUseResponse.ChairCurrentlyInUse> mappedUtilizations =
                utilizationService.loadAllChairsCurrentlyInUse(spaceId);

        LoadSeatsCurrentlyInUseResponse response =
                LoadSeatsCurrentlyInUseResponse.builder()
                        .isThisSpaceCurrentlyInUse(false)
                        .allChairsCurrentlyInUse(mappedUtilizations)
                        .build();

        return response;
    }
}
