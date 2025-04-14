package shop.sgmarket.sgmarketbackend.global.security;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.TOKEN_ROLE_NAME;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import shop.sgmarket.sgmarketbackend.auth.domain.RefreshToken;
import shop.sgmarket.sgmarketbackend.auth.dto.AccessTokenDto;
import shop.sgmarket.sgmarketbackend.auth.dto.RefreshTokenDto;
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

    public void generateTokenPair(final Long memberId,
                                               final MemberRole memberRole,
                                               HttpServletResponse response) {
        String accessToken = createAccessToken(memberId, memberRole);
        String refreshToken = createRefreshToken(memberId);

        HttpHeaders cookieHeaders = cookieUtil.generateTokenCookies(accessToken, refreshToken);
        for (String cookie : Objects.requireNonNull(cookieHeaders.get(HttpHeaders.SET_COOKIE))) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        }
    }



    private String createAccessToken(final Long memberId,final MemberRole memberRole) {
        return jwtUtil.generateAccessToken(memberId, memberRole);
    }

    public AccessTokenDto createAccessTokenDto(final Long memberId, final MemberRole memberRole) {
        return jwtUtil.generateAccessTokenDto(memberId, memberRole);
    }

    private String createRefreshToken(final Long memberId) {
        String refreshTokenValue = jwtUtil.generateRefreshToken(memberId);
        saveRefreshTokenToRedis(memberId, refreshTokenValue, jwtUtil.getRefreshTokenExpirationTime());
        return refreshTokenValue;
    }

    private void saveRefreshTokenToRedis(final Long memberId, final String refreshTokenDto, final Long ttl) {
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .memberId(memberId)
                        .token(refreshTokenDto)
                        .ttl(ttl)
                        .build();
        refreshTokenRepository.save(refreshToken);
    }

    public AccessTokenDto retrieveAccessToken(final String accessTokenValue) {
        try {
            return jwtUtil.parseAccessToken(accessTokenValue);
        } catch (Exception e) {
            log.info("Access Token 파싱 실패");
            return null;
        }
    }

    public RefreshTokenDto retrieveRefreshToken(final String refreshTokenValue) {
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

    private Optional<RefreshToken> getRefreshTokenFromRedis(final Long memberId) {
        return refreshTokenRepository.findById(memberId);
    }

    private RefreshTokenDto parseRefreshToken(final String refreshTokenValue) {
        try {
            return jwtUtil.parseRefreshToken(refreshTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    public AccessTokenDto reissueAccessTokenIfExpired(final String accessTokenValue) {
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
