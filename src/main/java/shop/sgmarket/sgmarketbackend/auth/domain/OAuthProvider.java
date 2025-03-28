package shop.sgmarket.sgmarketbackend.auth.domain;

import java.security.InvalidParameterException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    ;
    private final String value;

    public static OAuthProvider from(String provider) {
        return switch (provider.toUpperCase()) {
            case "KAKAO" -> KAKAO;
            case "GOOGLE" -> GOOGLE;
            default -> throw new InvalidParameterException("[ERROR]: 지원하지 않는 제공자입니다: " + provider);
        };
    }
}
