package shop.sgmarket.sgmarketbackend.auction.domain;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.global.domain.BaseTimeEntity;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
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
    private Long endPrice;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private AuctionCategory category;

    @Column(name = "like_count")
    private long likeCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private Auction(String title, String description, LocalDateTime startDate, LocalDateTime endDate,
                    long startPrice, long currentPrice, Long endPrice, Double latitude,
                    Double longitude, AuctionCategory category, long likeCount, Item item, Member member,
                    AuctionStatus status) {
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
        this.likeCount = likeCount;
        this.item = item;
        this.member = member;
        this.status = status;
    }

    public static Auction create(String title, String description, LocalDateTime endDate,
                                 long startPrice, Double latitude, Double longitude,
                                 AuctionCategory category, Item item, Member member) {
        return Auction.builder()
                .title(title)
                .description(description)
                .startDate(LocalDateTime.now())
                .endDate(endDate)
                .startPrice(startPrice)
                .currentPrice(0)
                .latitude(latitude)
                .longitude(longitude)
                .category(category)
                .likeCount(0L)
                .item(item)
                .member(member)
                .status(AuctionStatus.BIDDING)
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
        this.status = AuctionStatus.DELETED;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void updateCurrentPrice(long bidPrice) {
        validateBidPrice(bidPrice);
        this.currentPrice = bidPrice;
    }

    private void validateBidPrice(long bidPrice) {
        if (this.currentPrice >= bidPrice) {
            throw new CustomException(ErrorCode.BID_PRICE_TOO_LOW_CURRENT_PRICE);
        }
        if (this.startPrice > bidPrice) {
            throw new CustomException(ErrorCode.BID_PRICE_TOO_LOW_STARTING_PRICE);
        }
    }
}
