package shop.sgmarket.sgmarketbackend.auth.application.google;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.GOOGLE_AUTH_URL;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.GOOGLE_USER_INFO_URL;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.TOKEN_PREFIX;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import shop.sgmarket.sgmarketbackend.auth.application.OAuthClient;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.google.GoogleAuthResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.properties.GoogleProperties;

@Slf4j
@Component("GOOGLE")
@RequiredArgsConstructor
public class GoogleClient implements OAuthClient {

    private final GoogleProperties googleProperties;
    private final RestClient restClient;

    @Override
    public OAuthTokenResponse getToken(final String code) {
        MultiValueMap<String, String> params = createParams(code);

        return restClient.post()
                .uri(GOOGLE_AUTH_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params)
                .exchange((request, response) -> {
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        log.error("구글 토큰 조회 실패, 상태 코드: {}", response.getStatusCode());
                        throw new CustomException(ErrorCode.GOOGLE_COMMUNICATION_ERROR);
                    }
                    return Objects.requireNonNull(response.bodyTo(OAuthTokenResponse.class));
                });
    }

    private MultiValueMap<String, String> createParams(final String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleProperties.clientId());
        params.add("client_secret", googleProperties.clientSecret());
        params.add("redirect_uri", googleProperties.redirectUri());
        params.add("code", code);
        return params;
    }

    @Override
    public SocialClientResponse getUserInfo(final String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }
        GoogleAuthResponse googleAuthResponse =
                restClient.get()
                        .uri(GOOGLE_USER_INFO_URL)
                        .header("Authorization", TOKEN_PREFIX + token)
                        .exchange((request, response) -> {
                            if (!response.getStatusCode().is2xxSuccessful()) {
                                throw new CustomException(ErrorCode.GOOGLE_COMMUNICATION_ERROR);
                            }
                            return Objects.requireNonNull(response.bodyTo(GoogleAuthResponse.class));
                        });

        return SocialClientResponse.of(
                googleAuthResponse.email(),
                googleAuthResponse.sub(),
                googleAuthResponse.name(),
                googleAuthResponse.picture()
        );
    }
}
