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
import shop.sgmarket.sgmarketbackend.auth.dto.response.AccessTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.repository.RefreshTokenRepository;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
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

    public void generateRefreshToken(final Long memberId,
                                        final MemberRole memberRole,
                                     HttpServletResponse response) {
        String refreshToken = createRefreshToken(memberId, memberRole);

        HttpHeaders cookieHeaders = cookieUtil.generateTokenCookies(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieHeaders.getFirst(HttpHeaders.SET_COOKIE));

    }

    public AccessTokenResponse generateAccessToken(final Long memberId,
                                                   final MemberRole memberRole) {
        String accessToken = createAccessToken(memberId, memberRole);

        return AccessTokenResponse.of(accessToken);
    }


    private String createAccessToken(final Long memberId, final MemberRole memberRole) {
        return jwtUtil.generateAccessToken(memberId, memberRole);
    }

    public AccessTokenDto createAccessTokenDto(final Long memberId, final MemberRole memberRole) {
        return jwtUtil.generateAccessTokenDto(memberId, memberRole);
    }

    private String createRefreshToken(final Long memberId, final MemberRole memberRole) {
        String refreshTokenValue = jwtUtil.generateRefreshToken(memberId, memberRole);
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
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Optional<RefreshToken> refreshToken = getRefreshTokenFromRedis(refreshTokenDto.memberId());
        if (refreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return refreshTokenDto;
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
