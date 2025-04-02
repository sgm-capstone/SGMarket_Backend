package shop.sgmarket.sgmarketbackend.auth.dto;

import lombok.Builder;
import shop.sgmarket.sgmarketbackend.member.domain.MemberRole;

@Builder
public record AccessTokenDto(
        Long memberId,
        MemberRole memberRole,
        String tokenValue
) {
    public static AccessTokenDto of(Long memberId, MemberRole memberRole, String tokenValue) {
        return AccessTokenDto.builder()
                .memberId(memberId)
                .memberRole(memberRole)
                .tokenValue(tokenValue)
                .build();
    }
}
