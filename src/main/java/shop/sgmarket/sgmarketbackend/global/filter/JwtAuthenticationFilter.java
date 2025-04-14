package shop.sgmarket.sgmarketbackend.global.filter;

import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.ACCESS_TOKEN_COOKIE_NAME;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.REFRESH_TOKEN_COOKIE_NAME;
import static shop.sgmarket.sgmarketbackend.global.constant.SecurityConstant.TOKEN_PREFIX;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import shop.sgmarket.sgmarketbackend.auth.dto.AccessTokenDto;
import shop.sgmarket.sgmarketbackend.auth.dto.RefreshTokenDto;
import shop.sgmarket.sgmarketbackend.global.security.JwtTokenProvider;
import shop.sgmarket.sgmarketbackend.global.security.PrincipalDetails;
import shop.sgmarket.sgmarketbackend.member.domain.MemberRole;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 쿠키 Access Token 가져오기
        String accessToken = extractAccessTokenFromCookie(request);

        if (accessToken != null) {
            AccessTokenDto accessTokenDto = jwtTokenProvider.retrieveAccessToken(accessToken);
            if (accessTokenDto != null) {
                setAuthenticationToContext(accessTokenDto.memberId(), accessTokenDto.memberRole());
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 2. 쿠키에서 Refresh Token 가져오기
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Refresh Token 감증
        RefreshTokenDto refreshTokenDto = jwtTokenProvider.retrieveRefreshToken(refreshToken);
        if (refreshTokenDto == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 엑세스 토큰 재발급
        AccessTokenDto newAccessToken = jwtTokenProvider.reissueAccessTokenIfExpired(accessToken);
        if (newAccessToken != null) {
            response.setHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + newAccessToken.tokenValue());
            setAuthenticationToContext(newAccessToken.memberId(), newAccessToken.memberRole());
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationToContext(Long memberId, MemberRole memberRole) {
        UserDetails userDetails = new PrincipalDetails(memberId, memberRole);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE_NAME))
                .map(Cookie::getValue)
                .orElse(null);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME))
                .map(Cookie::getValue)
                .orElse(null);
    }
}
