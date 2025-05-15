package shop.sgmarket.sgmarketbackend.auction.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import shop.sgmarket.sgmarketbackend.auction.dto.request.BidRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.BidInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@Tag(name = "입찰 API", description = "입찰 관련 API입니다.")
public interface BidDocs {

    @Operation(
            summary = "입찰 등록",
            description = "특정 경매에 대해 입찰을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "입찰이 완료되었습니다."),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 경매가 존재하지 않습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    ApiResponseTemplate<BidInfoResponse> bid(
            @Parameter(description = "입찰할 경매 ID", required = true)
            Long auctionId,

            @Parameter(
                    description = "입찰 요청 데이터(JSON)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BidRegisterRequest.class))
            )
            BidRegisterRequest bidRegisterRequest
    );

    @Operation(
            summary = "경매 입찰 목록 조회",
            description = "특정 경매에 대한 입찰 내역을 조회합니다. " +
                    "입찰 내역은 최신순으로 자동 정렬되며, 정렬 파라미터를 따로 줄 필요가 없습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "입찰 목록 조회에 성공했습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 경매가 존재하지 않습니다.")
            }
    )
    ApiResponseTemplate<SliceResponse<BidInfoResponse>> getBidsForAuction(
            @Parameter(description = "조회할 경매 ID", required = true)
            Long auctionId,

            @Parameter(description = "페이지네이션 정보 (정렬은 최신순으로 고정됩니다)")
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "낙찰 처리",
            description = "경매 종료 시 낙찰자를 확정합니다. 해당 경매의 최고 입찰자를 낙찰자로 설정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "낙찰 처리가 완료되었습니다."),
                    @ApiResponse(responseCode = "400", description = "경매가 아직 종료되지 않았거나 낙찰 대상자가 없습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 경매가 존재하지 않습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    ApiResponseTemplate<BidInfoResponse> settleBid(
            @Parameter(description = "낙찰 처리할 경매 ID", required = true)
            Long auctionId
    );
}
