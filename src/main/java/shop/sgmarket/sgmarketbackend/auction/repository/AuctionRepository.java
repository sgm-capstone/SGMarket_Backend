package shop.sgmarket.sgmarketbackend.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
