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
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "COMMON_4003", "지원하지 않는 소셜 로그인 제공자입니다."),
    INVALID_KAKAO_TOKEN(HttpStatus.BAD_REQUEST, "KAKAO_4001", "카카오 토큰이 유효하지 않거나 비어 있습니다."),
    INVALID_GOOGLE_TOKEN(HttpStatus.BAD_REQUEST, "GOOGLE_4001", "구글 토큰이 유효하지 않거나 비어 있습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "IMAGE_4001", "파일 이름에 확장자가 없습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "AUTH_4011", "인증이 필요합니다."),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "AUTH_4012", "작성자가 아닙니다"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH_4013", "리프레시 토큰이 유효하지 않습니다."),

    // 403 FORBIDDEN
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "AUTH_4031", "접근 권한이 없습니다."),

    // 404 NOT FOUND
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4041", "회원을 찾을 수 없습니다."),
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "AUCTION_4041", "경매를 찾을 수 없습니다."),
    AUCTION_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "AUCTION_4042", "경매 카테고리를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_4041", "리프레시 토큰을 찾을 수 없습니다."),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_5001", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_5002", "데이터베이스 오류가 발생했습니다."),
    KAKAO_COMMUNICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "KAKAO_5001", "카카오 통신에 실패하였습니다."),
    GOOGLE_COMMUNICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GOOGLE_5001", "구글 통신에 실패하였습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_5001", "이미지 업로드에 실패했습니다."),
    MISSING_S3_BUCKET_CONFIG(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_5002", "S3 bucket 설정이 비어 있습니다."),
    MISSING_S3_REGION_CONFIG(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_5003", "S3 region 설정이 비어 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
