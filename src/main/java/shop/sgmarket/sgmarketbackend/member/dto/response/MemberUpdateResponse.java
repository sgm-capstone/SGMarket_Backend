package shop.sgmarket.sgmarketbackend.member.dto.response;

import lombok.Builder;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Builder
public record MemberRegisterResponse(
        Long memberId,
        String email,
        String nickname,
        String profileImageUrl,
        String memberRole,
        String address
) {
    public static MemberRegisterResponse from(final Member member) {
        return MemberRegisterResponse.builder()
                .memberId(member.getId())
                .email(member.getOauthInfo().getOauthEmail())
                .nickname(member.getOauthInfo().getOauthNickname())
                .profileImageUrl(member.getOauthInfo().getOauthProfileImageUrl())
                .memberRole(member.getRole().getValue())
                .address(member.getAddress())
                .build();
    }
}
