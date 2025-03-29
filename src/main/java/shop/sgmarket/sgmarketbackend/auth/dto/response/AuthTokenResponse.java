package shop.sgmarket.sgmarketbackend.auth.dto.response;

public record AuthTokenResponse(
        String accessToken
) {
    public static AuthTokenResponse of(
            TokenPairResponse tokenPairResponse) {
        return new AuthTokenResponse(
                tokenPairResponse.accessToken()
        );
    }
}
