package shop.sgmarket.sgmarketbackend.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
