package shop.sgmarket.sgmarketbackend.auction.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.global.domain.BaseTimeEntity;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bid extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Auction auction;

    private long price;

    @Builder(access = AccessLevel.PRIVATE)
    private Bid(Member member, Auction auction, long price) {
        this.member = member;
        this.auction = auction;
        this.price = price;
    }

    public static Bid createBid(Member member, Auction auction, long price) {
        return Bid.builder()
                .member(member)
                .auction(auction)
                .price(price)
                .build();
    }
}

