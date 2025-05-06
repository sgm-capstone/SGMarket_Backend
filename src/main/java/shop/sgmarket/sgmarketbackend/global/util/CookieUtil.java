package shop.sgmarket.sgmarketbackend.global.util;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.REFRESH_TOKEN_COOKIE_NAME;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.properties.JwtProperties;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtProperties jwtProperties;

    public HttpHeaders generateTokenCookies(final String refreshToken) {
        String sameSite = determineSameSitePolicy();

        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                        .path("/")
                        .secure(true)
                        .sameSite(sameSite)
                        .httpOnly(true)
                        .maxAge(jwtProperties.refreshTokenExpirationTime())
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private String determineSameSitePolicy() {
        return SameSite.NONE.attributeValue();
    }

    public static String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        if (cookie == null || cookie.getValue() == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        return cookie.getValue();
    }
}
