package shop.sgmarket.sgmarketbackend.auction.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.auction.application.AuctionLikeService;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionToggleLikeResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionLikeController {
    private final AuctionLikeService auctionLikeService;

    @PostMapping("/{auctionId}/like")
    public ApiResponseTemplate<AuctionToggleLikeResponse> toggleLike(
            @PathVariable Long auctionId
    ) {
        AuctionToggleLikeResponse response = auctionLikeService.toggleLike(auctionId);

        return ApiResponseTemplate.ok("경매 좋아요 토글에 성공했습니다.", response);
    }
}
