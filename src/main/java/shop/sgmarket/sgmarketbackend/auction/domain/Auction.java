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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.global.domain.BaseTimeEntity;
import shop.sgmarket.sgmarketbackend.global.domain.Status;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

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

    @Column(nullable = false)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @Column(nullable = false)
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private AuctionCategory category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder(access = AccessLevel.PRIVATE)
    private Auction(String title, String description, LocalDateTime startDate, LocalDateTime endDate,
                    long startPrice, long currentPrice, long endPrice, Double latitude,
                    Double longitude, AuctionCategory category, Item item, Member member, Status status) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.endPrice = endPrice;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.item = item;
        this.member = member;
        this.status = status;
    }

    public static Auction create(String title, String description, LocalDateTime endDate,
                                 long startPrice, long currentPrice, long endPrice, Double latitude, Double longitude,
                                 AuctionCategory category, Item item, Member member) {
        return Auction.builder()
                .title(title)
                .description(description)
                .startDate(LocalDateTime.now())
                .endDate(endDate)
                .startPrice(startPrice)
                .currentPrice(currentPrice)
                .endPrice(endPrice)
                .latitude(latitude)
                .longitude(longitude)
                .category(category)
                .item(item)
                .member(member)
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
