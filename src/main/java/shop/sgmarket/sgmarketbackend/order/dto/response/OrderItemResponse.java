package shop.sgmarket.sgmarketbackend.order.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.order.domain.OrderItem;

@Getter
@NoArgsConstructor
public class OrderItemResponse {
    private Long itemId;
    private int  quantity;
    private long price;

    public static OrderItemResponse from(OrderItem oi) {
        OrderItemResponse res = new OrderItemResponse();
        res.itemId  = oi.getItem().getId();
        res.quantity = oi.getQuantity();
        res.price    = oi.getPrice();
        return res;
    }
}
