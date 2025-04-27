package shop.sgmarket.sgmarketbackend.auth.application.kakao;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.KAKAO_AUTH_URL;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.KAKAO_USER_ME_URL;
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
import shop.sgmarket.sgmarketbackend.auth.dto.response.kakao.KakaoAuthResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.properties.KakaoProperties;

@Slf4j
@Component("KAKAO")
@RequiredArgsConstructor
public class KakaoClient implements OAuthClient {

    private final KakaoProperties kakaoProperties;
    private final RestClient restClient;

    @Override
    public OAuthTokenResponse getToken(final String code) {
        MultiValueMap<String, String> params = createParams(code);

        return restClient.post()
                .uri(KAKAO_AUTH_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params)
                .exchange((request, response) -> {
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        log.error("카카오 토큰 조회 실패, 상태 코드: {}", response.getStatusCode());
                        throw new CustomException(ErrorCode.KAKAO_COMMUNICATION_ERROR);
                    }
                    return Objects.requireNonNull(response.bodyTo(OAuthTokenResponse.class));
                });
    }

    private MultiValueMap<String, String> createParams(final String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.clientId());
        params.add("redirect_uri", kakaoProperties.redirectUri());
        params.add("code", code);
        return params;
    }

    @Override
    public SocialClientResponse getUserInfo(final String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_KAKAO_TOKEN);
        }
        KakaoAuthResponse kakaoAuthResponse =
                restClient
                        .get()
                        .uri(KAKAO_USER_ME_URL)
                        .header("Authorization", TOKEN_PREFIX + token)
                        .exchange(
                                (request, response) -> {
                                    if (!response.getStatusCode().is2xxSuccessful()) {
                                        throw new CustomException(ErrorCode.KAKAO_COMMUNICATION_ERROR);
                                    }
                                    return Objects.requireNonNull(
                                            response.bodyTo(KakaoAuthResponse.class));
                                });

        return SocialClientResponse.of(
                kakaoAuthResponse.kakaoAccount().email(),
                kakaoAuthResponse.id().toString(),
                kakaoAuthResponse.properties().nickname(),
                kakaoAuthResponse.properties().profileImage()
        );
    }
}
