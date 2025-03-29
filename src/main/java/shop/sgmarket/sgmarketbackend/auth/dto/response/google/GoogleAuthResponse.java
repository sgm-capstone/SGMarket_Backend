package shop.sgmarket.sgmarketbackend.auth.dto.response.google;

public record GoogleAuthResponse(
        String sub,
        String email,
        String name,
        String picture
) {
}
