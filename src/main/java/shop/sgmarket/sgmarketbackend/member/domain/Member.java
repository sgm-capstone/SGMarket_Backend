package shop.sgmarket.sgmarketbackend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OauthInfo oauthInfo;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String address;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(OauthInfo oauthInfo, MemberRole role, String address, String nickname, Status status) {
        this.oauthInfo = oauthInfo;
        this.role = role;
        this.address = address;
        this.nickname = nickname;
        this.status = status;
    }

    public static Member createOauthMember(final OAuthProvider oAuthProvider,
                                           final String oauthId,
                                           final String oauthEmail,
                                           final String oauthNickname,
                                           final String oauthProfileImageUrl
    ) {
        OauthInfo oauthInfo = OauthInfo.createOauthInfo(
                oauthId,
                oAuthProvider.getValue(),
                oauthEmail,
                oauthNickname,
                oauthProfileImageUrl
        );

        return Member.builder()
                .oauthInfo(oauthInfo)
                .role(MemberRole.USER)
                .status(Status.ACTIVE)
                .build();
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateProfile(final String address, final String nickname) {
        this.address = address;
        this.nickname = nickname;
    }
}
