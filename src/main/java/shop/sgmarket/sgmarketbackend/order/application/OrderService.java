package shop.sgmarket.sgmarketbackend.order.application;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auction.domain.Item;
import shop.sgmarket.sgmarketbackend.auction.domain.PriceHistory;
import shop.sgmarket.sgmarketbackend.auction.repository.ItemRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.PriceHistoryRepository;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.repository.MemberRepository;
import shop.sgmarket.sgmarketbackend.order.domain.Order;
import shop.sgmarket.sgmarketbackend.order.domain.OrderItem;
import shop.sgmarket.sgmarketbackend.order.domain.repository.OrderRepository;
import shop.sgmarket.sgmarketbackend.order.dto.request.OrderRequest;
import shop.sgmarket.sgmarketbackend.order.dto.response.OrderResponse;
import shop.sgmarket.sgmarketbackend.payment.domain.Payment;
import shop.sgmarket.sgmarketbackend.payment.domain.Status;
import shop.sgmarket.sgmarketbackend.payment.domain.repository.PaymentRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        /* 1. 회원 조회 */
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        /* 2. 주문 엔티티 생성 (PENDING 상태) */
        Order order = Order.builder()
                .member(member)
                .status(Status.PENDING)
                .orderUid(UUID.randomUUID().toString())   // 주문 UID 생성
                .build();

        for (OrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            Item item = itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

            long latestPrice = priceHistoryRepository
                    .findTopByItemIdOrderByRecordedAtDesc(item.getId())
                    .map(PriceHistory::getPrice)
                    .orElseThrow(() -> new EntityNotFoundException("가격 정보가 없습니다."));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .item(item)
                    .quantity(itemRequest.getQuantity())
                    .price((int) (latestPrice * itemRequest.getQuantity()))
                    .build();

            order.addOrderItem(orderItem);
        }

        /* 3-3. 총 주문 금액 계산 */
        long totalPrice = order.getOrderItems().stream()
                .mapToLong(OrderItem::getPrice)
                .sum();
        
        /* 3-4. Payment 객체 생성 및 연결 */
        Payment payment = Payment.builder()
                .price(totalPrice)
                .status(Status.PENDING)
                .build();
        paymentRepository.save(payment);
        
        /* 3-5. Order에 Payment 연결 */
        order.connectPayment(payment);
        
        /* 4. 주문 저장 */
        orderRepository.save(order);
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        order.cancel();
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getMemberOrders(Long memberId) {
        return orderRepository.findByMemberId(memberId)
                .stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }
}
