package shop.sgmarket.sgmarketbackend.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.auction.domain.Item;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;
    private int price;

    @Builder
    public OrderItem(Order order, Item item, int quantity, int price) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItem assignToOrder(Order order) {
        this.order = order;
        return this;
    }
}
