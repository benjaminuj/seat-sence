package project.seatsence.global.code;

import static org.springframework.http.HttpStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 에러 및 성공 코드 관리 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    /** 2xx : 성공 */
    SUCCESS(OK, "200_OK", "요청에 성공하였습니다."),
    SUCCESS_NO_CONTENT(NO_CONTENT, "204_NO_CONTENT", "요청한 데이터가 존재하지 않습니다"),

    /** 4xx : Client 오류 */
    INVALID_FIELD_VALUE(BAD_REQUEST, "400_BAD_REQUEST", "잘못된 요청입니다."),
    ENUM_VALUE_NOT_FOUND(NOT_FOUND, "404_ENUM_VALUE_NOT_FOUND", "존재하지 않는 ENUM 값입니다."),

    /** 5xx : Server 오류 */
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "500_INTERNAL_SERVER_ERROR", "서버와의 연결에 실패하였습니다."),

    /** Store error code */
    STORE_NOT_FOUND(NOT_FOUND, "STORE_404_001", "가게를 찾을 수 없습니다."),
    STORE_SORT_FIELD_NOT_FOUND(NOT_FOUND, "STORE_404_002", "가게의 정렬 조건을 적용할 수 없습니다"),
    STORE_CHAIR_NOT_FOUND(NOT_FOUND, "STORE_CHAIR_404_001", "가게 의자를 찾을 수 없습니다."),
    STORE_SPACE_NOT_FOUND(NOT_FOUND, "STORE_SPACE_404_001", "가게 스페이스를 찾을 수 없습니다."),

    /** Auth error code */
    TOKEN_IS_NULL(BAD_REQUEST, "AUTH_400_001", "Token 정보가 입력되지 않았습니다."),
    ACCESS_TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_001", "Access Token이 만료되었습니다."),
    ACCESS_TOKEN_TAMPERED(UNAUTHORIZED, "AUTH_401_002", "Access Token이 변조되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_401_003", "잘못된 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(FORBIDDEN, "AUTH_403_001", "리프레시 토큰이 만료되었습니다."),
    GENERATE_ACCESS_TOKEN_FAIL(INTERNAL_SERVER_ERROR, "AUTH_500_001", "Access Token 생성에 실패했습니다."),

    /** User error code */
    USER_EMAIL_ALREADY_EXIST(BAD_REQUEST, "USER_400_001", "해당 이메일로 가입된 유저가 이미 존재합니다."),
    USER_NICKNAME_ALREADY_EXIST(BAD_REQUEST, "USER_400_002", "해당 닉네임으로 가입된 유저가 이미 존재합니다."),
    USER_MISMATCHED_PASSWORD(BAD_REQUEST, "USER_400_003", "비밀번호가 일치하지 않습니다."),
    INACTIVE_USER(FORBIDDEN, "USER_403_001", "휴면계정입니다."),
    USER_NOT_FOUND(NOT_FOUND, "USER_404_001", "해당 user를 찾을 수 없습니다."),

    /** Admin error code */
    ADMIN_INFO_NOT_FOUND(NOT_FOUND, "ADMIN_INFO_404_001", "찾을 수 없는 사업자 정보입니다."),

    /** Utilization error code */
    INVALID_UTILIZATION_TIME(BAD_REQUEST, "UTILIZATION_400_001", "요청된 이용 시간이 유효하지 않습니다."),
    UTILIZATION_NOT_FOUND(NOT_FOUND, "UTILIZATION_404_001", "이용 내역을 찾을 수 없습니다."),

    /** Reservation error code */
    INVALID_RESERVATION_UNIT(BAD_REQUEST, "RESERVATION_400_001", "설정된 예약 단위가 해당 요청에 유효하지 않습니다."),

    INVALID_RESERVATION_STATUS(BAD_REQUEST, "RESERVATION_400_002", "유효하지 않은 예약 상태값 입니다."),
    INVALID_TIME_TO_MODIFY_RESERVATION_STATUS(
            BAD_REQUEST, "RESERVATION_400_003", "예약 상태를 관리할 수 없는 시간입니다."),
    RESERVATION_NOT_FOUND(NOT_FOUND, "RESERVATION_404_001", "예약 내역을 찾을 수 없습니다."),

    /** Walk in error code */
    WALK_IN_NOT_FOUND(NOT_FOUND, "WALK_IN_404_001", "바로사용 내역을 찾을 수 없습니다."),

    /** Store Member error code */
    STORE_MEMBER_ALREADY_EXIST(BAD_REQUEST, "STORE_MEMBER_400_001", "해당 이메일로 등록된 직원이 이미 존재합니다."),
    STORE_MEMBER_NOT_FOUND(NOT_FOUND, "STORE_MEMBER_404_001", "직원을 찾을 수 없습니다."),

    /** Store Custom Utilization Field error code */
    CUSTOM_UTILIZATION_FIELD_NOT_FOUND(
            NOT_FOUND, "CUSTOM_UTILIZATION_FIELD_404_001", "이용 커스텀 필드를 찾을 수 없습니다."),

    /** Participation error code */
    PARTICIPATION_NOT_FOUND(NOT_FOUND, "PARTICIPATION_404_001", "참여 내역을 찾을 수 없습니다."),
    INVALID_SELF_PARTICIPATION_APPLICATION(
            BAD_REQUEST, "PARTICIPATION_400_001", "신청자 본인은 참여 신청 할 수 없습니다."),
    USER_ALREADY_APPLY(BAD_REQUEST, "PARTICIPATION_400_002", "이미 참여 신청한 유저입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
