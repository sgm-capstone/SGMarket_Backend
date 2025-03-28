package shop.sgmarket.sgmarketbackend.member.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OauthInfo oauthInfo;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime lastLoginAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(OauthInfo oauthInfo, MemberRole memberRole, String address, Status status) {
        this.oauthInfo = oauthInfo;
        this.memberRole = memberRole;
        this.address = address;
        this.status = status;
    }

    public static Member createMember(OAuthProvider oAuthProvider,
                                      String oauthId,
                                      String email,
                                      String nickname,
                                      String profileImageUrl
    ) {
        OauthInfo oauthInfo = OauthInfo.createOauthInfo(oauthId, oAuthProvider.getValue(), email, nickname, profileImageUrl);
        return Member.builder()
                .oauthInfo(oauthInfo)
                .memberRole(MemberRole.USER)
                .status(Status.ACTIVE)
                .build();
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
