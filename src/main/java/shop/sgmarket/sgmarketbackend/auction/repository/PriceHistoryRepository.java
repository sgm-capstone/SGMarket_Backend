package shop.sgmarket.sgmarketbackend.auction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.PriceHistory;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findAllByItemId(Long itemId);
    Optional<PriceHistory> findTopByItemIdOrderByRecordedAtDesc(Long itemId);
}
