package shop.sgmarket.sgmarketbackend.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenPairResponse(
        String accessToken,
        String refreshToken) {

    public static TokenPairResponse of(final String accessToken, final String refreshToken) {
        return TokenPairResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
