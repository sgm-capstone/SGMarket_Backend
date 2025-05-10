package shop.sgmarket.sgmarketbackend.auction.dto.request;

import lombok.Builder;

@Builder
public record AuctionToggleLikeResponse(
        Long auctionId,
        boolean isLiked,
        Long likeCount
) {
    public static AuctionToggleLikeResponse of(Long auctionId, boolean isLiked, Long likeCount) {
        return AuctionToggleLikeResponse.builder()
                .auctionId(auctionId)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}
