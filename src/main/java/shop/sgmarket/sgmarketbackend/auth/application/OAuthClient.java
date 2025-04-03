package shop.sgmarket.sgmarketbackend.auth.application;

import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;

public interface OAuthClient {

    /**
     * 소셜 로그인 인증
     * oauth access token을 통해 사용자 정보를 가져온다.
     * @param token 인증 토큰
     * @return 소셜 로그인 사용자 정보
     */
    SocialClientResponse authenticate(String token);

    /**
     * 소셜 로그인 인증
     * auth code를 통해 access token과 refresh token을 발급받는다.
     * @param code 인증 코드
     * @return OAuth access 토큰과 refresh 토큰
     */
    OAuthTokenResponse getToken(String code);
}
