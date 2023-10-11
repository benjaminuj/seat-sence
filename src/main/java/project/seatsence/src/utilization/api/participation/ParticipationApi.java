package project.seatsence.src.utilization.api.participation;

import static project.seatsence.global.constants.Constants.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import project.seatsence.global.config.security.JwtProvider;
import project.seatsence.global.response.SliceResponse;
import project.seatsence.src.store.service.StoreService;
import project.seatsence.src.utilization.domain.Participation.Participation;
import project.seatsence.src.utilization.dto.request.participation.UserParticipationRequest;
import project.seatsence.src.utilization.dto.response.participation.StoreParticipationListResponse;
import project.seatsence.src.utilization.dto.response.participation.UserParticipationListResponse;
import project.seatsence.src.utilization.service.participation.ParticipationService;

@RestController
@RequestMapping("/v1/participation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "11. [Participation]")
public class ParticipationApi {

    private final ParticipationService participationService;
    private final StoreService storeService;

    @Operation(summary = "유저 스페이스 참여 전 내역")
    @GetMapping("/my-list/upcoming")
    public SliceResponse<UserParticipationListResponse.ParticipationResponse>
            getUserSpaceUpcomingParticipationList(
                    @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                    @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME)
                            String refreshToken,
                    @ParameterObject @PageableDefault(page = 1, size = 15) Pageable pageable) {

        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);
        Slice<Participation> participationSlice =
                participationService.getUpcomingParticipation(userEmail, pageable);

        SliceResponse<UserParticipationListResponse.ParticipationResponse> sliceResponse =
                participationService.getSpace(participationSlice);

        return sliceResponse;
    }

    @Operation(summary = "유저 스페이스 참여 완료 내역")
    @GetMapping("/my-list/participated")
    public SliceResponse<UserParticipationListResponse.ParticipationResponse>
            getUserSpaceParticipatedList(
                    @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                    @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME)
                            String refreshToken,
                    @ParameterObject @PageableDefault(page = 1, size = 15) Pageable pageable) {

        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);
        Slice<Participation> participationSlice =
                participationService.getParticipated(userEmail, pageable);

        SliceResponse<UserParticipationListResponse.ParticipationResponse> sliceResponse =
                participationService.getSpace(participationSlice);

        return sliceResponse;
    }

    @Operation(summary = "유저 스페이스 참여 취소")
    @DeleteMapping("/cancel/{participation-id}")
    public void cancelParticipation(@PathVariable("participation-id") Long participationId) {
        participationService.cancelParticipation(participationId);
    }

    // 예약 또는 바로사용 시간 보다 안 지나면
    @Operation(summary = "가게 스페이스 참여 가능 리스트")
    @GetMapping("/{store-id}/space-participation-list")
    public SliceResponse<StoreParticipationListResponse.SpaceResponse>
            getStoreParticipationSpaceList(
                    @PathVariable("store-id") Long storeId,
                    @ParameterObject @PageableDefault(page = 1, size = 15) Pageable pageable) {

        SliceResponse<StoreParticipationListResponse.SpaceResponse> sliceResponse =
                participationService.getParticipationSpace(storeId, pageable);

        return sliceResponse;
    }

    @Operation(summary = "가게 스페이스 참여")
    @PostMapping("/space-participation")
    public void inputSpaceParticipation(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME) String refreshToken,
            @Valid @RequestBody UserParticipationRequest userParticipationRequest) {

        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);
        participationService.inputSpaceParticipation(userEmail, userParticipationRequest);
    }
}
