package shop.sgmarket.sgmarketbackend.member.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.member.dto.request.ChargeCoinRequest;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberInfoResponse;

@Tag(name = "회원 API", description = "회원 관련 API입니다.")
public interface MemberDocs {

    @Operation(
            summary = "회원 정보 조회",
            description = "현재 로그인한 회원의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
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
                    @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @PatchMapping
    ApiResponseTemplate<MemberInfoResponse> updateProfile(
            @Parameter(hidden = true)
            @RequestBody @Valid MemberUpdateRequest memberUpdateRequest
    );

    @Operation(
            summary = "내 경매 목록 조회",
            description = "현재 로그인한 회원이 등록한 경매 목록을 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "내 경매 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = SliceResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @GetMapping("/auctions")
    ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getMyAuctions(
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "내가 좋아요한 경매 목록 조회",
            description = "현재 로그인한 회원이 좋아요한 경매 목록을 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "내가 좋아요한 경매 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = SliceResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @GetMapping("/auctions-likes")
    ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getMyLikedAuctions(
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "내가 입찰한 경매 목록 조회",
            description = "현재 로그인한 회원이 입찰한 경매 목록을 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "내가 입찰한 경매 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = SliceResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @GetMapping("/auctions-bids")
    ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getMyBiddedAuctions(
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "코인 충전",
            description = "현재 로그인한 회원의 코인을 충전합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "충전할 금액 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChargeCoinRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "코인 충전 성공",
                            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @PatchMapping("/charge-coin")
    ApiResponseTemplate<MemberInfoResponse> chargeCoin(
            @Parameter(hidden = true)
            @RequestBody @Valid ChargeCoinRequest chargeCoinRequest
    );

    @Operation(
            summary = "특정 회원 정보 조회",
            description = "회원 ID를 통해 특정 회원의 정보를 조회합니다. 관리자용 기능으로 사용됩니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "조회할 회원 ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 회원을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @GetMapping("/{memberId}")
    ApiResponseTemplate<MemberInfoResponse> getMemberInfoById(@PathVariable Long memberId);

    @Operation(
            summary = "특정 멤버의 경매 목록 조회",
            description = "특정 멤버가 등록한 경매 목록을 페이지 단위로 조회합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "조회할 멤버의 ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "해당 멤버의 경매 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = SliceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @GetMapping("/{memberId}/auctions")
    ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getAuctionsByMemberId(
            @PathVariable Long memberId,
            @ParameterObject Pageable pageable
    );
}
