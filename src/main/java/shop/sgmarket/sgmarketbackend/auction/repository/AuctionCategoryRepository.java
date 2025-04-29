package shop.sgmarket.sgmarketbackend.auction.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;

public interface AuctionCategoryRepository extends JpaRepository<AuctionCategory, Long> {
    Optional<AuctionCategory> findByName(String name);
}

