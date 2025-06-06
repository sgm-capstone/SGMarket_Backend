package shop.sgmarket.sgmarketbackend.order.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sgmarket.sgmarketbackend.order.domain.Order;
import shop.sgmarket.sgmarketbackend.payment.domain.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long memberId;
    private List<OrderItemResponse> orderItems;
    private String orderUid;
    private Status status;
    private LocalDateTime orderDate;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.id = order.getId();
        response.memberId = order.getMember().getId();
        response.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());
        response.orderUid = order.getOrderUid();
        response.status = order.getStatus();
        response.orderDate = order.getOrderDate();
        return response;
    }
}
