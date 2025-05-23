package shop.sgmarket.sgmarketbackend.payment.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Getter
@ToString
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentWebhookDto {
    // 아임포트 결제 고유번호
    private String impUid;
    
    // 가맹점 주문번호
    private String merchantUid;
    
    // 결제 상태
    private String status;
    
    // 결제 금액
    private Integer paidAmount;
    
    // 결제 수단
    private String pay_method;
    
    // 결제 시간
    private long paid_at;
    
    // 결제 실패 시 오류 메시지
    private String fail_reason;
    
    // 취소 금액
    private int cancel_amount;
    
    // 취소 사유
    private String cancel_reason;
}
