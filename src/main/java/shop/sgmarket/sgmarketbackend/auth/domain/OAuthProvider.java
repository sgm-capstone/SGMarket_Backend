package shop.sgmarket.sgmarketbackend.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    ;
    private final String value;

    public static OAuthProvider from(final String provider) {
        return switch (provider.toUpperCase()) {
            case "KAKAO" -> KAKAO;
            case "GOOGLE" -> GOOGLE;
            default -> throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER, ": " + provider);
        };
    }
}
