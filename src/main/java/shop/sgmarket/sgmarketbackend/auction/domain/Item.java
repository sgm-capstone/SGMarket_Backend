package shop.sgmarket.sgmarketbackend.auction.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.global.domain.BaseTimeEntity;
import shop.sgmarket.sgmarketbackend.global.domain.Status;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder(access = AccessLevel.PRIVATE)
    private Item(String name) {
        this.name = name;
        this.status = Status.ACTIVE;
    }

    public static Item createItem(String name) {
        return Item.builder()
                .name(name)
                .build();
    }

    public void update(String name) {
        this.name = name;
    }
}
