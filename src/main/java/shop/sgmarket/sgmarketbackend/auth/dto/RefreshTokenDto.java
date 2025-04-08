package shop.sgmarket.sgmarketbackend.auth.dto;

public record RefreshTokenDto(
        Long memberId,
        String tokenValue,
        Long ttl
) {
}
