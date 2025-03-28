package shop.sgmarket.sgmarketbackend.auth.application.google;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.GOOGLE_AUTH_URL;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.GOOGLE_USER_INFO_URL;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.TOKEN_PREFIX;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import shop.sgmarket.sgmarketbackend.auth.application.OAuthClient;
import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.goggle.GoogleAuthResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.properties.GoogleProperties;

@Component("GOOGLE")
@RequiredArgsConstructor
@EnableConfigurationProperties(GoogleProperties.class)
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
                        throw new CustomException(ErrorCode.GOOGLE_COMMUNICATION_ERROR);
                    }
                    return Objects.requireNonNull(response.bodyTo(OAuthTokenResponse.class));
                });
    }

    private MultiValueMap<String, String> createParams(final String code) {
        return new LinkedMultiValueMap<>() {{
            add("grant_type", "authorization_code");
            add("client_id", googleProperties.clientId());
            add("client_secret", googleProperties.clientSecret());
            add("redirect_uri", googleProperties.redirectUri());
            add("code", code);
        }};
    }

    @Override
    public SocialClientResponse authenticate(final String token) {
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

        return new SocialClientResponse(
                googleAuthResponse.email(),
                googleAuthResponse.sub(),
                googleAuthResponse.name(),
                googleAuthResponse.picture()
        );
    }
}
