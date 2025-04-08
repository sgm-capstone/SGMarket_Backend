package shop.sgmarket.sgmarketbackend.global.constant;

public final class SecurityConstant {

    // kakao
    public static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/token";
    public static final String KAKAO_USER_ME_URL = "https://kapi.kakao.com/v2/user/me";

    // google
    public static final String GOOGLE_AUTH_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    // security
    public static final String TOKEN_ROLE_NAME = "role";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private SecurityConstant() {}
}
