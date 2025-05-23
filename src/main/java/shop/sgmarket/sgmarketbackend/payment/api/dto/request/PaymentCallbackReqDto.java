package shop.sgmarket.sgmarketbackend.payment.api.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentCallbackReqDto(
        String paymentUid,  // imp_uid (결제 고유번호)
        String orderUid     // merchant_uid (주문 고유번호)
) {
}