package shop.sgmarket.sgmarketbackend.order.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.order.application.OrderService;
import shop.sgmarket.sgmarketbackend.order.dto.request.OrderRequest;
import shop.sgmarket.sgmarketbackend.order.dto.response.OrderResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "주문 API", description = "주문 생성 · 조회 · 취소")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    public ApiResponseTemplate<OrderResponse> createOrder(
            @Parameter(description = "주문 생성 요청 DTO", required = true)
            @RequestBody OrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(request);
        return ApiResponseTemplate.created("주문 생성에 성공했습니다.", response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 단건 조회", description = "orderId로 주문을 조회합니다.")
    public ApiResponseTemplate<OrderResponse> getOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable Long orderId
    ) {
        OrderResponse response = orderService.getOrder(orderId);
        return ApiResponseTemplate.ok("주문 조회에 성공했습니다.", response);
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "orderId로 주문을 취소합니다.")
    public ApiResponseTemplate<OrderResponse> cancelOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable Long orderId
    ) {
        OrderResponse response = orderService.cancelOrder(orderId);
        return ApiResponseTemplate.ok("주문 취소에 성공했습니다.", response);
    }

    @GetMapping("/members/{memberId}")
    @Operation(
        summary = "회원별 주문 목록 조회", 
        description = "특정 회원의 모든 주문 목록을 조회합니다."
    )
    public ApiResponseTemplate<List<OrderResponse>> getMemberOrders(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long memberId) {
        List<OrderResponse> response = orderService.getMemberOrders(memberId);
        return ApiResponseTemplate.ok("회원의 주문 목록 조회에 성공했습니다.", response);
    }
}
