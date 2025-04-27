package shop.sgmarket.sgmarketbackend.auction.dto.response;

import java.util.List;

public record AuctionsResponse(
        List<AuctionInfoResponse> auctions
) {
    public static AuctionsResponse from(List<AuctionInfoResponse> auctions) {
        return new AuctionsResponse(auctions);
    }
}
