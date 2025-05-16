package shop.sgmarket.sgmarketbackend.auction.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Slice<Bid> findAllByAuction(Auction auction, Pageable pageable);
    Optional<Bid> findTopByAuctionOrderByCreatedAtDesc(Auction auction);
    Optional<Bid> findTopByAuctionOrderByPriceDesc(Auction auction);
}
