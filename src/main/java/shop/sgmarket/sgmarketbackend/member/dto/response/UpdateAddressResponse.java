package shop.sgmarket.sgmarketbackend.member.dto.response;

import lombok.Builder;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Builder
public record UpdateAddressResponse(
        Long memberId,
        String email,
        String nickname,
        String profileImageUrl,
        String memberRole,
        String address
) {
    public static UpdateAddressResponse from(Member member) {
        return UpdateAddressResponse.builder()
                .memberId(member.getId())
                .email(member.getOauthInfo().getEmail())
                .nickname(member.getOauthInfo().getEmail())
                .profileImageUrl(member.getOauthInfo().getProfileImageUrl())
                .memberRole(member.getMemberRole().getValue())
                .address(member.getAddress())
                .build();
    }
}
