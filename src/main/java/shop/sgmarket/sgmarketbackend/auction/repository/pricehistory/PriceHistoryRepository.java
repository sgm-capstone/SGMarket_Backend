package shop.sgmarket.sgmarketbackend.auction.repository.pricehistory;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.PriceHistory;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long>, PriceHistoryRepositoryCustom {
    Optional<PriceHistory> findTopByItemIdOrderByRecordedAtDesc(Long itemId);
}
