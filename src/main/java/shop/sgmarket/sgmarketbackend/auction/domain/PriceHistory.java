package shop.sgmarket.sgmarketbackend.auction.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PriceHistory {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    private long price;

    private LocalDateTime recordedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private PriceHistory(Item item, long price, LocalDateTime recordedAt) {
        this.item = item;
        this.price = price;
        this.recordedAt = recordedAt;
    }

    public static PriceHistory createPriceHistory(Item item, long price) {
        return PriceHistory.builder()
                .item(item)
                .price(price)
                .recordedAt(LocalDateTime.now())
                .build();
    }
}
