package shop.sgmarket.sgmarketbackend.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthTokenResponse(
        String accessToken
) {
    public static AuthTokenResponse of(
            final TokenPairResponse tokenPairResponse) {
        return AuthTokenResponse.builder()
                .accessToken(tokenPairResponse.accessToken())
                .build();
    }
}
