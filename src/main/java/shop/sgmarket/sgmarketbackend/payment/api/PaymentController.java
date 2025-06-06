package shop.sgmarket.sgmarketbackend.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.payment.api.dto.response.PaymentResDto;
import shop.sgmarket.sgmarketbackend.payment.application.PaymentService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "결제 API", description = "Portone(아임포트) 결제 연동")
public class PaymentController {

    @Value("${imp.api.client-code}")
    private String clientCode;

    private final PaymentService paymentService;

    @PostMapping
    @Operation(
            summary     = "결제 정보 조회",
            description = "orderUid를 받아 결제창 호출에 필요한 clientCode·금액 등을 반환합니다."
    )
    public ApiResponseTemplate<Map<String, Object>> createPayment(
            @Parameter(description = "주문 UID(merchant_uid)", required = true)
            @RequestParam String orderUid
    ) {

        PaymentResDto paymentInfo = paymentService.getPaymentInfo(orderUid);

        Map<String, Object> result = new HashMap<>();
        result.put("clientCode", clientCode);
        result.put("paymentInfo", paymentInfo);

        return ApiResponseTemplate.ok("결제 정보 조회에 성공했습니다.", result);
    }
}
