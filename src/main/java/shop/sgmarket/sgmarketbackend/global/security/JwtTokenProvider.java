package shop.sgmarket.sgmarketbackend.global.security;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.TOKEN_ROLE_NAME;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import shop.sgmarket.sgmarketbackend.auth.domain.RefreshToken;
import shop.sgmarket.sgmarketbackend.auth.dto.AccessTokenDto;
import shop.sgmarket.sgmarketbackend.auth.dto.RefreshTokenDto;
import shop.sgmarket.sgmarketbackend.auth.dto.response.TokenPairResponse;
import shop.sgmarket.sgmarketbackend.auth.repository.RefreshTokenRepository;
import shop.sgmarket.sgmarketbackend.global.util.CookieUtil;
import shop.sgmarket.sgmarketbackend.global.util.JwtUtil;
import shop.sgmarket.sgmarketbackend.member.domain.MemberRole;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenPairResponse generateTokenPair(Long memberId, MemberRole memberRole, HttpServletResponse response) {
        String accessToken = createAccessToken(memberId, memberRole);
        String refreshToken = createRefreshToken(memberId);

        HttpHeaders cookieHeaders = cookieUtil.generateTokenCookies(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieHeaders.getFirst(HttpHeaders.SET_COOKIE));


        return TokenPairResponse.of(accessToken, refreshToken);
    }


    private String createAccessToken(Long memberId, MemberRole memberRole) {
        return jwtUtil.generateAccessToken(memberId, memberRole);
    }

    public AccessTokenDto createAccessTokenDto(Long memberId, MemberRole memberRole) {
        return jwtUtil.generateAccessTokenDto(memberId, memberRole);
    }

    private String createRefreshToken(Long memberId) {
        String token = jwtUtil.generateRefreshToken(memberId);
        saveRefreshTokenToRedis(memberId, token, jwtUtil.getRefreshTokenExpirationTime());
        return token;
    }

    private void saveRefreshTokenToRedis(Long memberId, String refreshTokenDto, Long ttl) {
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .memberId(memberId)
                        .token(refreshTokenDto)
                        .ttl(ttl)
                        .build();
        refreshTokenRepository.save(refreshToken);
    }

    public AccessTokenDto retrieveAccessToken(String accessTokenValue) {
        try {
            return jwtUtil.parseAccessToken(accessTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    public RefreshTokenDto retrieveRefreshToken(String refreshTokenValue) {
        RefreshTokenDto refreshTokenDto = parseRefreshToken(refreshTokenValue);

        if (refreshTokenDto == null) {
            return null;
        }

        Optional<RefreshToken> refreshToken = getRefreshTokenFromRedis(refreshTokenDto.memberId());

        if (refreshToken.isPresent()) {
            return refreshTokenDto;
        }

        return null;
    }

    private Optional<RefreshToken> getRefreshTokenFromRedis(Long memberId) {
        return refreshTokenRepository.findById(memberId);
    }

    private RefreshTokenDto parseRefreshToken(String refreshTokenValue) {
        try {
            return jwtUtil.parseRefreshToken(refreshTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    public AccessTokenDto reissueAccessTokenIfExpired(String accessTokenValue) {
        // AT가 만료된 경우 AT 재발급, 만료되지 않은 경우 null 반환
        try {
            jwtUtil.parseAccessToken(accessTokenValue);
            return null;
        } catch (ExpiredJwtException e) {
            Long memberId = Long.parseLong(e.getClaims().getSubject());
            MemberRole memberRole =
                    MemberRole.valueOf(e.getClaims().get(TOKEN_ROLE_NAME, String.class));
            return createAccessTokenDto(memberId, memberRole);
        }
    }
}
