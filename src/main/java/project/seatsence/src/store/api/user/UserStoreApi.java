package project.seatsence.src.store.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.dto.mapper.StoreMapper;
import project.seatsence.src.store.dto.response.user.StoreDetailResponse;
import project.seatsence.src.store.dto.response.user.StoreListResponse;
import project.seatsence.src.store.service.StoreService;

@RequestMapping("/v1/stores/users")
@RestController
@RequiredArgsConstructor
@Tag(name = "04. [Store - User]")
public class UserStoreApi {

    private final StoreService storeService;
    private final StoreMapper storeMapper;

    @Operation(summary = "사용자 - 가게 리스트 받아오기")
    @GetMapping("/list")
    public StoreListResponse getStores(
            @Parameter(
                            description = "가게의 카테고리(전체 - 생략, 나머지 - 특정 카테고리)",
                            name = "category",
                            schema =
                                    @Schema(
                                            allowableValues = {"음식점", "카페", "모임"},
                                            nullable = true))
                    @RequestParam(required = false)
                    String category,
            @Parameter(
                            description =
                                    "page - 1부터 시작, size - 한 페이지에 담을 데이터 수, sort - 정렬 조건, 순서대로 적용",
                            name = "pageable",
                            required = true)
                    Pageable pageable) {
        Page<Store> storePage = storeService.findAllByState(category, pageable);
        return StoreListResponse.builder()
                .curCount(storePage.getNumberOfElements())
                .curPage(pageable.getPageNumber() + 1)
                .totalCount(storePage.getTotalElements())
                .totalPage(storePage.getTotalPages())
                .storeResponseList(
                        storePage.getContent().stream()
                                .map(
                                        store ->
                                                StoreListResponse.StoreResponse.builder()
                                                        .id(store.getId())
                                                        .name(store.getStoreName())
                                                        .introduction(store.getIntroduction())
                                                        .address(store.getAddress())
                                                        .detailAddress(store.getDetailAddress())
                                                        .mainImage(
                                                                storeService.getStoreMainImage(
                                                                        store.getId()))
                                                        .isOpen(storeService.isOpenNow(store))
                                                        .telNum(store.getTelNum())
                                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    @Operation(summary = "사용자 - 가게 정보 가져오기")
    @GetMapping("/{store-id}")
    public StoreDetailResponse getStore(@PathVariable("store-id") Long storeId) {
        Store store = storeService.findByIdAndState(storeId);
        return storeMapper.toDto(store);
    }

    @GetMapping("/search/name")
    @Operation(summary = "사용자 - 가게 이름으로 검색하기")
    public StoreListResponse getStoresByName(
            @Parameter(description = "가게의 이름을 포함하는 결과 검색", name = "name", required = true)
                    @RequestParam
                    String name,
            @Parameter(
                            description =
                                    "page - 1부터 시작, size - 한 페이지에 담을 데이터 수, sort - 정렬 조건, 순서대로 적용",
                            name = "pageable",
                            required = true)
                    Pageable pageable) {
        Page<Store> findAllByName = storeService.findAllByNameAndState(name, pageable);
        return StoreListResponse.builder()
                .curCount(findAllByName.getNumberOfElements())
                .curPage(pageable.getPageNumber() + 1)
                .totalCount(findAllByName.getTotalElements())
                .totalPage(findAllByName.getTotalPages())
                .storeResponseList(
                        findAllByName.getContent().stream()
                                .map(
                                        store ->
                                                StoreListResponse.StoreResponse.builder()
                                                        .id(store.getId())
                                                        .name(store.getStoreName())
                                                        .introduction(store.getIntroduction())
                                                        .address(store.getAddress())
                                                        .detailAddress(store.getDetailAddress())
                                                        .mainImage(
                                                                storeService.getStoreMainImage(
                                                                        store.getId()))
                                                        .isOpen(storeService.isOpenNow(store))
                                                        .telNum(store.getTelNum())
                                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }
}
