package shop.sgmarket.sgmarketbackend.auth.application;

import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;

public interface OAuthClient {
    SocialClientResponse authenticate(String token);
    OAuthTokenResponse getToken(String code);
}
