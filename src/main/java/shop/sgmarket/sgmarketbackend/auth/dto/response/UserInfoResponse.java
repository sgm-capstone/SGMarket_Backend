package shop.sgmarket.sgmarketbackend.auth.dto.response;

public record UserInfoResponse(
        String nickname,
        String profileImage
) {
    public static UserInfoResponse of(String nickname, String profileImage) {
        return new UserInfoResponse(nickname, profileImage);
    }
}