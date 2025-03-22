package shop.sgmarket.sgmarketbackend.global.error;

public record ErrorResponse(int httpStatus, String name, String code, String message) {

    public static ErrorResponse of(int httpStatus, String name, String code, String message) {
        return new ErrorResponse(httpStatus, name, code, message);
    }
}