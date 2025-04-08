package shop.sgmarket.sgmarketbackend.global.error;

public record ErrorResponse(int httpStatus, String name, String code, String message) {

    public static ErrorResponse of(final int httpStatus, final String name, final String code, final String message) {
        return new ErrorResponse(httpStatus, name, code, message);
    }
}