package shop.sgmarket.sgmarketbackend.auth.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import shop.sgmarket.sgmarketbackend.auth.dto.response.AccessTokenResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.response.AuthTokenResponse;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthDocs {

    @Hidden
    @Operation(summary = "소셜 로그인", description = "소셜 제공자와 인증 코드를 사용하여 로그인을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "소셜 로그인 성공",
                            content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    void socialLogin (
            @Parameter(description = "소셜 제공자", required = true) String provider,
            @Parameter(description = "인증 코드", required = true) String code,
            HttpServletResponse response
    ) throws IOException;

    @PostMapping("/access-token")
    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰을 통해 새로운 액세스 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재발급 성공",
                            content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    AccessTokenResponse reissueAccessToken(HttpServletRequest request);
}
