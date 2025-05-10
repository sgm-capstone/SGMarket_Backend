package shop.sgmarket.sgmarketbackend.auction.dto.response;

import lombok.Builder;

@Builder
public record AuctionToggleLikeResponse(
        Long auctionId,
        boolean isLiked,
        long likeCount
) {
    public static AuctionToggleLikeResponse of(Long auctionId, boolean isLiked, long likeCount) {
        return AuctionToggleLikeResponse.builder()
                .auctionId(auctionId)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}
