package shop.sgmarket.sgmarketbackend.member.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberInfoResponse;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberUpdateResponse;

@Tag(name = "회원 API", description = "회원 관련 API입니다.")
public interface MemberDocs {

    @Operation(
            summary = "회원 정보 조회",
            description = "현재 로그인한 회원의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @GetMapping
    ApiResponseTemplate<MemberInfoResponse> getMemberInfo();

    @Operation(
            summary = "회원 프로필 수정",
            description = "회원의 닉네임, 주소 등을 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 수정 요청 데이터",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MemberUpdateRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 수정 성공", content = @Content(schema = @Schema(implementation = MemberUpdateResponse.class))),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @PatchMapping
    ApiResponseTemplate<MemberUpdateResponse> updateProfile(
            @Parameter(hidden = true)
            @RequestBody @Valid MemberUpdateRequest memberUpdateRequest
    );
}
