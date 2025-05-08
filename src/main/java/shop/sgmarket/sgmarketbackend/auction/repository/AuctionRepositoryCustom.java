package shop.sgmarket.sgmarketbackend.auction.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.global.domain.Status;

public interface AuctionRepositoryCustom {
    Slice<Auction> findAuctionsWithinRadius(
            double lat,
            double lng,
            double radiusKm,
            Status status,
            String categoryName,
            Pageable pageable
    );
}


