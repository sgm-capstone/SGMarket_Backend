package shop.sgmarket.sgmarketbackend.member.dto.response;

import lombok.Builder;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Builder
public record MemberInfoResponse(
        Long id,
        String email,
        String profileImageUrl,
        String nickname,
        String address,
        Double latitude,
        Double longitude
) {
    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .id(member.getId())
                .email(member.getOauthInfo().getOauthEmail())
                .profileImageUrl(member.getOauthInfo().getOauthProfileImageUrl())
                .nickname(member.getNickname())
                .address(member.getLocation().getAddress())
                .latitude(member.getLocation().getLatitude())
                .longitude(member.getLocation().getLongitude())
                .build();
    }
}
