package shop.sgmarket.sgmarketbackend.global.util;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.ACCESS_TOKEN_COOKIE_NAME;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.REFRESH_TOKEN_COOKIE_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import shop.sgmarket.sgmarketbackend.global.properties.CookieProperties;
import shop.sgmarket.sgmarketbackend.global.properties.JwtProperties;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtProperties jwtProperties;
    private final CookieProperties cookieProperties;

    public HttpHeaders generateTokenCookies(final String accessToken, final String refreshToken) {
        String sameSite = determineSameSitePolicy();

        ResponseCookie accessTokenCookie =
                ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
                        .path("/")
                        .secure(cookieProperties.isSecure())
                        .sameSite(sameSite)
                        .httpOnly(true)
                        .maxAge(jwtProperties.accessTokenExpirationTime())
                        .build();

        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                        .path("/")
                        .secure(cookieProperties.isSecure())
                        .sameSite(sameSite)
                        .httpOnly(true)
                        .maxAge(jwtProperties.refreshTokenExpirationTime())
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private String determineSameSitePolicy() {
        return Cookie.SameSite.NONE.attributeValue();  // 일단 임시 설정
    }
}
