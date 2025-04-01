package shop.sgmarket.sgmarketbackend.global.error.exception;

import lombok.Getter;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Object additionalMessage) {
        super(errorCode.getMessage() + " : " + additionalMessage);
        this.errorCode = errorCode;
    }
}
