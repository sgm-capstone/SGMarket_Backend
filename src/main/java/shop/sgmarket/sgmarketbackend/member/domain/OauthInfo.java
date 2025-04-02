package shop.sgmarket.sgmarketbackend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthInfo {

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @Column(name = "email")
    private String oauthEmail;

    @Column(name = "oauth_nickname")
    private String oauthNickname;

    @Column(name = "profile_image_url")
    private String oauthProfileImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private OauthInfo(String oauthId, String oauthProvider, String oauthEmail, String oauthNickname, String oauthProfileImageUrl) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        this.oauthEmail = oauthEmail;
        this.oauthNickname = oauthNickname;
        this.oauthProfileImageUrl = oauthProfileImageUrl;
    }

    public static OauthInfo createOauthInfo(final String oauthId,
                                            final String oauthProvider,
                                            final String email,
                                            final String nickname,
                                            final String profileImageUrl) {
        return OauthInfo.builder()
                .oauthId(oauthId)
                .oauthProvider(oauthProvider)
                .oauthEmail(email)
                .oauthNickname(nickname)
                .oauthProfileImageUrl(profileImageUrl)
                .build();
    }
}
