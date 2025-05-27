package shop.sgmarket.sgmarketbackend.member.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;
import shop.sgmarket.sgmarketbackend.global.domain.BaseTimeEntity;
import shop.sgmarket.sgmarketbackend.global.domain.Status;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OauthInfo oauthInfo;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Embedded
    private MemberLocation location;

    private String nickname;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Auction> auctions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(OauthInfo oauthInfo, MemberRole role, MemberLocation location, String nickname, Status status) {
        this.oauthInfo = oauthInfo;
        this.role = role;
        this.location = location;
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

    public void updateProfile(final MemberLocation location, final String nickname) {
        this.location = location;
        this.nickname = nickname;
    }
}
