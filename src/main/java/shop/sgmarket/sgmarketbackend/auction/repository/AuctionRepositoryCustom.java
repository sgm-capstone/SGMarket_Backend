package shop.sgmarket.sgmarketbackend.auction.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionStatus;

public interface AuctionRepositoryCustom {
    Slice<Auction> findAuctionsWithinRadius(
            double lat,
            double lng,
            double radiusKm,
            AuctionCategory category,
            Pageable pageable
    );
    Optional<Long> findItemIdByAuctionId(Long auctionId);
    Optional<Auction> findRandomAuctionByStatus(AuctionStatus status);
}
