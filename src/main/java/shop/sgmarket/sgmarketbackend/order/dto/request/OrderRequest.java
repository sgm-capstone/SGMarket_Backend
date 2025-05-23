package shop.sgmarket.sgmarketbackend.order.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequest {
    private Long memberId;
    private List<OrderItemRequest> orderItems;

    @Getter
    @NoArgsConstructor
    public static class OrderItemRequest {
        private Long itemId;
        private int quantity;
    }
}