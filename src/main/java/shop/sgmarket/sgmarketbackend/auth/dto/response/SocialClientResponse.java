package shop.sgmarket.sgmarketbackend.auth.dto.response;

import lombok.Builder;

@Builder
public record SocialClientResponse(
        String email,
        String oauthId,
        String nickname,
        String profileImage
) {
    public static SocialClientResponse of(
            String email,
            String oauthId,
            String nickname,
            String profileImage
    ) {
        return new SocialClientResponse(
                email,
                oauthId,
                nickname,
                profileImage
        );
    }
}
