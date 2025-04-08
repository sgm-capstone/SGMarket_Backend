package shop.sgmarket.sgmarketbackend.member.dto.response;

import lombok.Builder;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Builder
public record MemberUpdateResponse(
        Long memberId,
        String email,
        String nickname,
        String profileImageUrl,
        String memberRole,
        String address
) {
    public static MemberUpdateResponse from(final Member member) {
        return MemberUpdateResponse.builder()
                .memberId(member.getId())
                .email(member.getOauthInfo().getOauthEmail())
                .nickname(member.getOauthInfo().getOauthNickname())
                .profileImageUrl(member.getOauthInfo().getOauthProfileImageUrl())
                .memberRole(member.getRole().getValue())
                .address(member.getAddress())
                .build();
    }
}
