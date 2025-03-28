package shop.sgmarket.sgmarketbackend.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import shop.sgmarket.sgmarketbackend.auth.dto.request.RefreshTokenRequest;
import shop.sgmarket.sgmarketbackend.auth.dto.response.AuthTokenResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthDocs {

    @Operation(summary = "소셜 로그인", description = "소셜 제공자와 인증 코드를 사용하여 로그인을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "소셜 로그인 성공",
                            content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    ApiResponseTemplate<AuthTokenResponse> socialLogin(
            @Parameter(description = "소셜 제공자", required = true) String provider,
            @Parameter(description = "인증 코드", required = true) String code,
            HttpServletResponse response
    );

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                            content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    ApiResponseTemplate<AuthTokenResponse> reissueTokenPair(
            @Parameter(description = "리프레시 토큰 요청", required = true) RefreshTokenRequest request,
            HttpServletResponse response
    );
}
