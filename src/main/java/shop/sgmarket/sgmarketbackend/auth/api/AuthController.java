package shop.sgmarket.sgmarketbackend.auth.api;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.auth.application.AuthService;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;
import shop.sgmarket.sgmarketbackend.auth.dto.response.OAuthTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.SocialClientResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthDocs {

    private final AuthService authService;

    @Override
    @GetMapping("/login")
    public void socialLogin(
            @RequestParam final String provider,
            @RequestParam final String code,
            HttpServletResponse response
    ) throws IOException {
        OAuthProvider oauthProvider = OAuthProvider.from(provider);
        OAuthTokenResponse oAuthTokenResponse = authService.getToken(oauthProvider, code);
        SocialClientResponse socialClientResponse = authService.authenticateFromProvider(oauthProvider,
                oAuthTokenResponse.accessToken());

        authService.socialLogin(
                oauthProvider,
                socialClientResponse.oauthId(),
                socialClientResponse.email(),
                socialClientResponse.nickname(),
                socialClientResponse.profileImage(),
                response
        );
    }

    // TODO: 로그아웃 API 구현
}
