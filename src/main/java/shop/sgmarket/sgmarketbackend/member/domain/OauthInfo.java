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
    public String oauthId;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private OauthInfo(String oauthId, String oauthProvider, String email, String nickname, String profileImageUrl) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static OauthInfo createOauthInfo(String oauthId,
                                            String oauthProvider,
                                            String email,
                                            String nickname,
                                            String profileImageUrl) {
        return OauthInfo.builder()
                .oauthId(oauthId)
                .oauthProvider(oauthProvider)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
