package shop.sgmarket.sgmarketbackend.auth.dto.response.goggle;

public record GoogleAuthResponse(
        String sub,
        String email,
        String name,
        String picture
) {
}
