package shop.sgmarket.sgmarketbackend.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.payment.domain.Payment;
import shop.sgmarket.sgmarketbackend.payment.domain.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime orderDate;

    /* PortOne(아임포트)와 매핑될 가맹점 주문번호 */
    @Column(name = "order_uid", unique = true, nullable = false)
    private String orderUid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    public Order(Member member, Status status, String orderUid) {
        this.member = member;
        this.status = status;
        this.orderUid = orderUid;
        this.orderDate = LocalDateTime.now();
    }

    public Order addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignToOrder(this);
        return this;
    }

    public void cancel() {
        this.status = Status.CANCELLED;
    }

    public void complete() {
        this.status = Status.PAID;
    }

    public Order connectPayment(Payment payment) {
        this.payment = payment;
        return this;
    }
}
