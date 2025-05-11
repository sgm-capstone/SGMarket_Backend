package shop.sgmarket.sgmarketbackend.auth.application;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;
import shop.sgmarket.sgmarketbackend.auth.dto.RefreshTokenDto;
import shop.sgmarket.sgmarketbackend.auth.dto.response.AccessTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.properties.RedirectUriProperties;
import shop.sgmarket.sgmarketbackend.global.security.JwtTokenProvider;
import shop.sgmarket.sgmarketbackend.global.util.CookieUtil;
import shop.sgmarket.sgmarketbackend.global.util.JwtUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<OAuthProvider, OAuthClient> oAuthClients;
    private final RedirectUriProperties redirectUriProperties;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Transactional(readOnly = true)
    public OAuthTokenResponse getToken(final OAuthProvider provider, final String code) {
        OAuthClient oAuthClient = oAuthClients.get(provider);
        return oAuthClient.getToken(code);
    }

    @Transactional(readOnly = true)
    public SocialClientResponse authenticateFromProvider(final OAuthProvider provider, final String token) {
        OAuthClient oAuthClient = oAuthClients.get(provider);
        if (oAuthClient == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER, provider);
        }
        return oAuthClient.getUserInfo(token);
    }

    @Transactional
    public void socialLogin(final OAuthProvider oAuthProvider,
                            final String oauthId,
                            final String email,
                            final String nickname,
                            final String profileImage,
                            HttpServletResponse response) throws IOException {

        Member member = memberRepository
                .findByOauthInfoOauthProviderAndOauthInfoOauthId(oAuthProvider.getValue(), oauthId)
                .orElseGet(() -> createOauthMember(oAuthProvider, oauthId, email, nickname, profileImage));

        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(member.getId(), MemberRole.USER);
        String refreshToken = jwtUtil.generateRefreshToken(member.getId());
        
        // Redis에 refresh token 저장
        saveRefreshTokenToRedis(String.valueOf(member.getId()), refreshToken, jwtUtil.getRefreshTokenExpirationTime());
        
        // 쿠키 설정
        HttpHeaders cookieHeaders = cookieUtil.generateTokenCookies(accessToken, refreshToken);
        for (String cookie : Objects.requireNonNull(cookieHeaders.get(HttpHeaders.SET_COOKIE))) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        }

        member.updateLastLoginAt();
        log.info("소셜 로그인 진행: {}", member.getId());
        
        // 리다이렉트 URL에 토큰 추가
        String redirectUri = redirectUriProperties.redirectUri();
        if (redirectUri.contains("?")) {
            redirectUri += "&";
        } else {
            redirectUri += "?";
        }
        redirectUri += "accessToken=" + accessToken + "&refreshToken=" + refreshToken;
        
        response.sendRedirect(redirectUri);
    }

    private void getLoginResponse(final Member member, HttpServletResponse response) {
        jwtTokenProvider.generateRefreshToken(member.getId(), member.getRole(), response);
    }

    private Member createOauthMember(final OAuthProvider oAuthProvider,
                                     final String oauthId,
                                     final String email,
                                     final String nickname,
                                     final String profileImage) {
        Member oauthMember = Member.createOauthMember(oAuthProvider, oauthId, email, nickname, profileImage);
        memberRepository.save(oauthMember);
        log.info("회원가입 진행: {}", oauthMember.getId());

        return oauthMember;
    }


    private void saveRefreshTokenToRedis(String userId, String refreshToken, long expirationTime) {
    @Transactional(readOnly = true)
    public AccessTokenResponse getAccessToken(HttpServletRequest request) {
        String refreshToken = CookieUtil.extractRefreshTokenFromCookie(request);
        validateRefreshToken(refreshToken);
        RefreshTokenDto refreshTokenDto = jwtTokenProvider.retrieveRefreshToken(refreshToken);

        return jwtTokenProvider.generateAccessToken(refreshTokenDto.memberId(), refreshTokenDto.memberRole());
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
