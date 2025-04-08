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
            final String email,
            final String oauthId,
            final String nickname,
            final String profileImage
    ) {
        return SocialClientResponse.builder()
                .email(email)
                .oauthId(oauthId)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}
