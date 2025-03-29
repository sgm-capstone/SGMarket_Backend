package shop.sgmarket.sgmarketbackend.auth.application;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;
import shop.sgmarket.sgmarketbackend.auth.dto.RefreshTokenDto;
import shop.sgmarket.sgmarketbackend.auth.dto.request.RefreshTokenRequest;
import shop.sgmarket.sgmarketbackend.auth.dto.response.AuthTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.TokenPairResponse;
import shop.sgmarket.sgmarketbackend.global.jwt.JwtTokenProvider;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.domain.MemberRole;
import shop.sgmarket.sgmarketbackend.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberUtil memberUtil;
    private final Map<OAuthProvider, OAuthClient> oAuthClients;

    @Transactional(readOnly = true)
    public OAuthTokenResponse getToken(OAuthProvider provider, String code) {
        OAuthClient client = oAuthClients.get(provider);
        return client.getToken(code);
    }

    @Transactional(readOnly = true)
    public SocialClientResponse authenticateFromProvider(OAuthProvider provider, String token) {
        OAuthClient client = oAuthClients.get(provider);
        if (client == null) {
            throw new IllegalArgumentException(provider + " 는 지원하지 않는 소셜 로그인입니다.");
        }
        return client.authenticate(token);
    }

    @Transactional
    public AuthTokenResponse socialLogin(OAuthProvider oAuthProvider,
                                         String oauthId,
                                         String email,
                                         String nickname,
                                         String profileImage,
                                         HttpServletResponse response) {

        Member member = memberRepository
                .findByOauthInfoOauthProviderAndOauthInfoOauthId(oAuthProvider.getValue(), oauthId)
                .orElseGet(() -> {
                    Member newMember = Member.createOauthMember(oAuthProvider, oauthId, email, nickname, profileImage);
                    memberRepository.save(newMember);
                    log.info("회원가입 진행: {}", newMember.getId());
                    return newMember;
                });

        TokenPairResponse tokenPair = getLoginResponse(member, response);
        member.updateLastLoginAt();
        log.info("소셜 로그인 진행: {}", member.getId());

        return AuthTokenResponse.of(tokenPair);

    }

    @Transactional(readOnly = true)
    public AuthTokenResponse reissueTokenPair(RefreshTokenRequest request, HttpServletResponse response) {
        RefreshTokenDto refreshTokenDto =
                jwtTokenProvider.retrieveRefreshToken(request.refreshToken());
        RefreshTokenDto refreshToken =
                jwtTokenProvider.createRefreshTokenDto(refreshTokenDto.memberId());

        Member member = memberUtil.getMemberByMemberId(refreshToken.memberId());

        TokenPairResponse tokenPair = getLoginResponse(member, response);
        return AuthTokenResponse.of(tokenPair);
    }

    private TokenPairResponse getLoginResponse(Member member, HttpServletResponse response) {
        return jwtTokenProvider.generateTokenPair(member.getId(), MemberRole.USER, response);
    }
}
