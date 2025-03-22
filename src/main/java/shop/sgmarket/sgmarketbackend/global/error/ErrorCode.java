package shop.sgmarket.sgmarketbackend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD REQUEST
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_4001", "입력 값이 올바르지 않습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_4002", "필수 요청 파라미터가 누락되었습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "AUTH_4011", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_4012", "아이디 또는 비밀번호가 올바르지 않습니다."),

    // 403 FORBIDDEN
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "AUTH_4031", "접근 권한이 없습니다."),

    // 404 NOT FOUND

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_5001", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_5002", "데이터베이스 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
