package shop.sgmarket.sgmarketbackend.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
