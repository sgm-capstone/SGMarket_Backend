package shop.sgmarket.sgmarketbackend.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import shop.sgmarket.sgmarketbackend.auth.dto.response.AuthTokenResponse;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthDocs {

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
}
