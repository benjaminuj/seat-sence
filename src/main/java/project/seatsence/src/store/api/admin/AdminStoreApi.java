package project.seatsence.src.store.api.admin;

import static project.seatsence.global.constants.Constants.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.seatsence.global.config.security.JwtProvider;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.dto.request.admin.basic.StoreBasicInformationRequest;
import project.seatsence.src.store.dto.request.admin.basic.StoreIsClosedTodayRequest;
import project.seatsence.src.store.dto.request.admin.basic.StoreNewBusinessInformationRequest;
import project.seatsence.src.store.dto.request.admin.basic.StoreOperatingTimeRequest;
import project.seatsence.src.store.dto.response.admin.basic.StoreBasicInformationResponse;
import project.seatsence.src.store.dto.response.admin.basic.StoreNewBusinessInformationResponse;
import project.seatsence.src.store.dto.response.admin.basic.StoreOperatingTimeResponse;
import project.seatsence.src.store.dto.response.admin.basic.StoreOwnedStoreResponse;
import project.seatsence.src.store.service.*;

@RequestMapping("/v1/stores/admins")
@RestController
@RequiredArgsConstructor
@Tag(name = "03 - 1. [Store - Admin]")
@Slf4j
@Validated
public class AdminStoreApi {

    private final StoreService storeService;

    @Operation(
            summary = "관리 권한이 있는 모든 가게 정보 가져오기",
            description = "isOpenNow : 영업 중 여부, isClosedToday : 오늘 영업 정지 여부")
    @GetMapping("/owned")
    public StoreOwnedStoreResponse getOwnedStore(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME) String refreshToken,
            @Parameter(
                            description =
                                    "page - 1부터 시작, size - 한 페이지에 담을 데이터 수(default 10), sort - 정렬 조건, 순서대로 적용(여기서는 id로 넣으시면 됩니다!)",
                            name = "pageable",
                            required = true)
                    Pageable pageable) {
        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);
        return storeService.findAllOwnedStore(userEmail, pageable);
    }

    @Operation(summary = "admin 가게 기본 정보 가져오기")
    @GetMapping("/basic-information/{store-id}")
    public StoreBasicInformationResponse getStoreBasicInformation(
            @PathVariable("store-id") Long storeId) {
        return storeService.getStoreBasicInformation(storeId);
    }

    @Operation(summary = "admin 가게 기본 정보 등록하기", description = "가게의 카테고리 - 음식점, 카페, 모임 중 선택")
    @PatchMapping("/basic-information/{store-id}")
    public void postStoreBasicInformation(
            @PathVariable("store-id") Long storeId,
            @RequestParam("storeName") String storeName,
            @RequestParam("address") String address,
            @RequestParam("detailAddress") String detailAddress,
            @RequestParam("category") String category,
            @RequestParam("introduction") String introduction,
            @RequestParam(value = "telNum", required = false) String telNum,
            @RequestParam(value = "originImages", required = false) List<String> originImages,
            @RequestParam(value = "file", required = false) List<MultipartFile> files)
            throws IOException {
        StoreBasicInformationRequest request =
                StoreBasicInformationRequest.createAdminStoreBasicInformationRequest(
                        storeName, address, detailAddress, category, introduction, telNum);
        storeService.updateBasicInformation(request, storeId, originImages, files);
    }

    @Operation(summary = "admin 가게 운영시간 정보 가져오기")
    @GetMapping("/operating-time/{store-id}")
    public StoreOperatingTimeResponse getStoreOperatingTime(
            @PathVariable("store-id") Long storeId) {
        Store store = storeService.findByIdAndState(storeId);
        return StoreOperatingTimeResponse.of(store);
    }

    @Operation(
            summary = "admin 가게 운영 시간 정보 등록하기",
            description = "오픈, 마감 시간은 12:00 형태로 요청, 브레이크타임은 12:00~14:00 형태로 요청해야합니다!")
    @PatchMapping("/operating-time/{store-id}")
    public void postStoreOperatingTime(
            @PathVariable("store-id") Long storeId,
            @RequestBody @Valid StoreOperatingTimeRequest request) {
        storeService.updateOperatingTime(request, storeId);
    }

    @Operation(summary = "admin 가게 오늘 영업 중단 여부 설정")
    @PatchMapping("/temporary-closed/{store-id}")
    public void patchStoreTemporaryClosed(
            @PathVariable("store-id") Long storeId,
            @RequestBody @Valid StoreIsClosedTodayRequest request) {
        storeService.updateIsClosedToday(request, storeId);
    }

    @Operation(summary = "admin 가게 정보 삭제하기")
    @DeleteMapping("/{store-id}")
    public void deleteStore(@PathVariable("store-id") Long storeId) {
        storeService.delete(storeId);
    }

    @Operation(summary = "어드민 사업자정보 추가")
    @PostMapping("/new-business-information")
    public StoreNewBusinessInformationResponse adminNewBusinessInformation(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME) String refreshToken,
            @Valid @RequestBody
                    StoreNewBusinessInformationRequest storeNewBusinessInformationRequest) {
        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);
        return storeService.adminNewBusinessInformation(
                userEmail, storeNewBusinessInformationRequest);
    }
}
