package shop.sgmarket.sgmarketbackend.auth.dto.response.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoAuthResponse(
        Long id,
        KakaoAccountResponse kakaoAccount,
        PropertiesResponse properties
) {
    public static record KakaoAccountResponse(
            String email
    ) {
    }

    public static record PropertiesResponse(
            String nickname, String profile_image
    ) {
    }
}
