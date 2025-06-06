package shop.sgmarket.sgmarketbackend.payment.api.dto.response;

import java.util.Optional;
import lombok.Builder;
import shop.sgmarket.sgmarketbackend.order.domain.Order;

@Builder
public record PaymentResDto(
        String buyerNickname,
        String buyerEmail,
        String buyerAddress,
        String productName,
        Long   price,
        String orderUid
) {
    public static PaymentResDto from(Order order) {

        /* ---------- 구매자 정보 ---------- */
        var member    = order.getMember();
        var oauth     = member.getOauthInfo();
        var location  = member.getLocation();

        String nickname = member.getNickname() != null
                ? member.getNickname()
                : (oauth == null ? null : oauth.getOauthNickname());

        String email   = oauth == null ? null : oauth.getOauthEmail();
        String address = location == null ? null : location.getAddress();

        /* ---------- 상품명 & 결제금액 ---------- */
        String productName = Optional.ofNullable(order.getOrderItems())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getItem().getName())
                .orElse("상품");

        Long price = order.getPayment() == null
                ? 0L
                : order.getPayment().getPrice();

        return PaymentResDto.builder()
                .buyerNickname(nickname)
                .buyerEmail(email)
                .buyerAddress(address)
                .productName(productName)
                .price(price)
                .orderUid(order.getOrderUid())
                .build();
    }
}
