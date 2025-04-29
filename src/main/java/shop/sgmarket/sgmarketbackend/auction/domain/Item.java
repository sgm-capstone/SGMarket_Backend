package shop.sgmarket.sgmarketbackend.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import shop.sgmarket.sgmarketbackend.global.domain.Status;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder(access = AccessLevel.PRIVATE)
    private Item(String name, String imageUrl, Member member) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.member = member;
        this.status = Status.ACTIVE;
    }

    public static Item createItem(String name, String imageUrl, Member member) {
        return Item.builder()
                .name(name)
                .imageUrl(imageUrl)
                .member(member)
                .build();
    }

    public void update(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
