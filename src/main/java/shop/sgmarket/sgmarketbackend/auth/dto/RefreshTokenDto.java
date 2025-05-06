package shop.sgmarket.sgmarketbackend.auth.dto;

import shop.sgmarket.sgmarketbackend.member.domain.MemberRole;

public record RefreshTokenDto(
        Long memberId,
        MemberRole memberRole,
        String tokenValue,
        Long ttl
) {
}
