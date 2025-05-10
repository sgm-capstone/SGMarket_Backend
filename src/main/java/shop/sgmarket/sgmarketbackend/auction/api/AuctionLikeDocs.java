package shop.sgmarket.sgmarketbackend.auction.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionToggleLikeResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@Tag(name = "[경매 좋아요 API]", description = "경매 좋아요 관련 API")
public interface AuctionLikeDocs {

    @Operation(
            summary = "경매 좋아요 토글",
            description = "특정 경매에 대해 좋아요를 추가하거나 제거합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "경매 좋아요 토글에 성공했습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 경매가 존재하지 않습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @PostMapping("/auctions/{auctionId}/like")
    ApiResponseTemplate<AuctionToggleLikeResponse> toggleLike(
            @Parameter(description = "좋아요를 토글할 경매 ID")
            @PathVariable Long auctionId
    );
}
