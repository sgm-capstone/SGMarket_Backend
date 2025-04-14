package shop.sgmarket.sgmarketbackend.auth.application;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.security.JwtTokenProvider;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.domain.MemberRole;
import shop.sgmarket.sgmarketbackend.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<OAuthProvider, OAuthClient> oAuthClients;

    @Transactional(readOnly = true)
    public OAuthTokenResponse getToken(final OAuthProvider provider, final String code) {
        OAuthClient client = oAuthClients.get(provider);
        return client.getToken(code);
    }

    @Transactional(readOnly = true)
    public SocialClientResponse authenticateFromProvider(final OAuthProvider provider, final String token) {
        OAuthClient oAuthClient = oAuthClients.get(provider);
        if (oAuthClient == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER, provider);
        }
        return oAuthClient.authenticate(token);
    }

    @Transactional
    public void socialLogin(final OAuthProvider oAuthProvider,
                                         final String oauthId,
                                         final String email,
                                         final String nickname,
                                         final String profileImage,
                                         HttpServletResponse response) {

        Member member = memberRepository
                .findByOauthInfoOauthProviderAndOauthInfoOauthId(oAuthProvider.getValue(), oauthId)
                .orElseGet(() -> createOauthMember(oAuthProvider, oauthId, email, nickname, profileImage));

        getLoginResponse(member, response);
        member.updateLastLoginAt();
        log.info("소셜 로그인 진행: {}", member.getId());
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

    private void getLoginResponse(Member member, HttpServletResponse response) {
        jwtTokenProvider.generateTokenPair(member.getId(), MemberRole.USER, response);
    }
}
