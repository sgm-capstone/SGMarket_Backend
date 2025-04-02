package shop.sgmarket.sgmarketbackend.auth.dto.response;

public record TokenPairResponse(
        String accessToken,
        String refreshToken) {

    public static TokenPairResponse of(final String accessToken, final String refreshToken) {
        return new TokenPairResponse(accessToken, refreshToken);
    }
}
