package project.seatsence.src.utilization.api.reservation;

import static project.seatsence.global.code.ResponseCode.*;
import static project.seatsence.global.constants.Constants.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.seatsence.global.config.security.JwtProvider;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.global.response.SliceResponse;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.store.domain.StoreSpace;
import project.seatsence.src.store.service.StoreChairService;
import project.seatsence.src.store.service.StoreService;
import project.seatsence.src.store.service.StoreSpaceService;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.user.service.UserService;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.reservation.ReservationStatus;
import project.seatsence.src.utilization.dto.request.ChairUtilizationRequest;
import project.seatsence.src.utilization.dto.request.SpaceUtilizationRequest;
import project.seatsence.src.utilization.dto.response.reservation.UserReservationListResponse;
import project.seatsence.src.utilization.service.UserUtilizationService;
import project.seatsence.src.utilization.service.reservation.ReservationService;
import project.seatsence.src.utilization.service.reservation.UserReservationService;

@RestController
@RequestMapping("/v1/reservations/users")
@Tag(name = "06. [Reservation - User]", description = "유저에 관한 예약 API")
@Validated
@RequiredArgsConstructor
public class UserReservationApi {
    private final UserReservationService userReservationService;
    private final UserUtilizationService userUtilizationService;
    private final StoreService storeService;
    private final StoreChairService storeChairService;
    private final StoreSpaceService storeSpaceService;
    private final UserService userService;
    private final ReservationService reservationService;

    @Operation(summary = "유저 의자 예약", description = "유저가 예약하고싶은 날짜의 특정 의자를 예약합니다.")
    @PostMapping("/chair")
    public void chairReservation(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME) String refreshToken,
            @Valid @RequestBody ChairUtilizationRequest chairUtilizationRequest)
            throws JsonProcessingException {

        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);
        StoreChair storeChairFound =
                storeChairService.findByIdAndState(chairUtilizationRequest.getStoreChairId());

        Store storeFound =
                storeService.findByIdAndState(storeChairFound.getStoreSpace().getStore().getId());

        if (storeSpaceService.reservationUnitIsOnlySpace(storeChairFound.getStoreSpace())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        if (!userReservationService.isPossibleReservationTimeUnit(
                chairUtilizationRequest.getStartSchedule(),
                chairUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        if (!userUtilizationService.isMoreThanMinimumUtilizationTime(
                chairUtilizationRequest.getStartSchedule(),
                chairUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        if (!userUtilizationService.isStartDateIsEqualEndDate(
                chairUtilizationRequest.getStartSchedule(),
                chairUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        if (!userUtilizationService.isStartScheduleIsBeforeEndSchedule(
                chairUtilizationRequest.getStartSchedule(),
                chairUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        // 당일예약 유효성 체크
        if (userReservationService.isSameDayReservation(
                chairUtilizationRequest.getStartSchedule())) {
            if (!userReservationService.isPossibleSameDayReservationStartSchedule(
                    chairUtilizationRequest.getStartSchedule())) {
                throw new BaseException(INVALID_UTILIZATION_TIME);
            }
        }

        User userFound = userService.findByEmailAndState(userEmail);

        Reservation reservation =
                Reservation.builder()
                        .store(storeFound)
                        .reservedStoreChair(storeChairFound)
                        .reservedStoreSpace(null)
                        .user(userFound)
                        .startSchedule(chairUtilizationRequest.getStartSchedule())
                        .endSchedule(chairUtilizationRequest.getEndSchedule())
                        .build();

        reservationService.save(reservation);

        // custom utilization content 관련
        userReservationService.inputChairCustomUtilizationContent(
                userFound, reservation, chairUtilizationRequest);
    }

    @Operation(summary = "유저 스페이스 예약", description = "유저가 예약하고싶은 날짜의 특정 스페이스를 예약합니다.")
    @PostMapping("/space")
    public void spaceReservation(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME) String refreshToken,
            @Valid @RequestBody SpaceUtilizationRequest spaceUtilizationRequest)
            throws JsonProcessingException {
        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);
        StoreSpace storeSpaceFound =
                storeSpaceService.findByIdAndState(spaceUtilizationRequest.getStoreSpaceId());
        Store storeFound = storeService.findByIdAndState(storeSpaceFound.getStore().getId());

        if (storeSpaceService.reservationUnitIsOnlySeat(storeSpaceFound)) {
            throw new BaseException(INVALID_RESERVATION_UNIT);
        }

        if (!userReservationService.isPossibleReservationTimeUnit(
                spaceUtilizationRequest.getStartSchedule(),
                spaceUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        if (!userUtilizationService.isMoreThanMinimumUtilizationTime(
                spaceUtilizationRequest.getStartSchedule(),
                spaceUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        if (!userUtilizationService.isStartDateIsEqualEndDate(
                spaceUtilizationRequest.getStartSchedule(),
                spaceUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        if (!userUtilizationService.isStartScheduleIsBeforeEndSchedule(
                spaceUtilizationRequest.getStartSchedule(),
                spaceUtilizationRequest.getEndSchedule())) {
            throw new BaseException(INVALID_UTILIZATION_TIME);
        }

        // 당일예약 유효성 체크
        if (userReservationService.isSameDayReservation(
                spaceUtilizationRequest.getStartSchedule())) {
            if (!userReservationService.isPossibleSameDayReservationStartSchedule(
                    spaceUtilizationRequest.getStartSchedule())) {
                throw new BaseException(INVALID_UTILIZATION_TIME);
            }
        }

        User userFound = userService.findByEmailAndState(userEmail);

        Reservation reservation =
                Reservation.builder()
                        .store(storeFound)
                        .reservedStoreChair(null)
                        .reservedStoreSpace(storeSpaceFound)
                        .user(userFound)
                        .startSchedule(spaceUtilizationRequest.getStartSchedule())
                        .endSchedule(spaceUtilizationRequest.getEndSchedule())
                        .build();

        reservationService.save(reservation);

        // custom utilization content 관련
        userReservationService.inputSpaceCustomUtilizationContent(
                userFound, reservation, spaceUtilizationRequest);
    }

    @Operation(
            summary = "유저 예약 현황 조회",
            description = "유저의 '예약 대기중', '승인된 예약', '거절된 예약', '취소한 예약'의 정보를 불러옵니다.")
    @GetMapping("/my-list")
    public SliceResponse<UserReservationListResponse> getUserReservationList(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(COOKIE_NAME_PREFIX_SECURE + REFRESH_TOKEN_NAME) String refreshToken,
            @Parameter(
                            description =
                                    "조회할 예약 상태값 : 입력 가능한 예약 상태값은 '대기', '취소', '승인', '거절'중 하나만 가능합니다.",
                            in = ParameterIn.QUERY,
                            example = "거절")
                    @RequestParam("reservation-status")
                    String reservationStatus,
            @ParameterObject @PageableDefault(page = 1, size = 15) Pageable pageable) {
        String userEmail = JwtProvider.getUserEmailFromValidToken(accessToken, refreshToken);

        return userReservationService.getUserReservationList(
                userEmail, ReservationStatus.valueOfKr(reservationStatus), pageable);
    }

    @Operation(summary = "유저 예약 취소", description = "유저가 예약했던 좌석 혹은 스페이스의 예약을 취소합니다.")
    @DeleteMapping("/{reservation-id}")
    public void cancelReservation(
            @Parameter(description = "예약 식별자", in = ParameterIn.PATH, example = "1")
                    @PathVariable("reservation-id")
                    Long reservationId) {
        Reservation reservation = reservationService.findByIdAndState(reservationId);

        userReservationService.cancelReservation(
                reservation); // Todo : Refactoring (parameter : object vs value)
    }
}
