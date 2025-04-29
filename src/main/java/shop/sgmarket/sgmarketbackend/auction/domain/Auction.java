package shop.sgmarket.sgmarketbackend.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.global.domain.BaseTimeEntity;
import shop.sgmarket.sgmarketbackend.global.domain.Status;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "start_price")
    private long startPrice;

    @Column(name = "current_price")
    private long currentPrice;

    @Column(name = "end_price")
    private long endPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AuctionCategory category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder(access = AccessLevel.PRIVATE)
    private Auction(String title, String description, LocalDateTime startDate, LocalDateTime endDate,
                    long startPrice, long currentPrice, long endPrice, AuctionCategory category, Item item, Status status) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.endPrice = endPrice;
        this.category = category;
        this.item = item;
        this.status = status;
    }

    public static Auction create(String title, String description, LocalDateTime endDate,
                                 long startPrice, long currentPrice, long endPrice, AuctionCategory category,
                                 Item item) {
        return Auction.builder()
                .title(title)
                .description(description)
                .startDate(LocalDateTime.now())
                .endDate(endDate)
                .startPrice(startPrice)
                .currentPrice(currentPrice)
                .endPrice(endPrice)
                .category(category)
                .item(item)
                .status(Status.ACTIVE)
                .build();
    }

    public void update(String title, String description, LocalDateTime endDate,
                       AuctionCategory category) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.category = category;
    }

    public void delete() {
        this.status = Status.DELETED;
    }
}
