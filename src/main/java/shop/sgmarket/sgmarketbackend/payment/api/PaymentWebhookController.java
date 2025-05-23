package shop.sgmarket.sgmarketbackend.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.ErrorResponse;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.payment.api.dto.request.PaymentCallbackReqDto;
import shop.sgmarket.sgmarketbackend.payment.api.dto.request.PaymentWebhookDto;
import shop.sgmarket.sgmarketbackend.payment.application.PaymentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments/webhook")
@Tag(name = "결제 웹훅 API", description = "Portone(아임포트) 결제 웹훅 처리")
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/portone")
    @Operation(
            summary     = "Portone 웹훅 처리",
            description = "결제 완료/취소 등 이벤트 발생 시 Portone에서 호출하는 웹훅을 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "웹훅 처리 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description  = "웹훅 처리 실패 (금액 불일치, 미완료 결제 등)",
                    content      = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<?> handlePortoneWebhook(
            @Parameter(description = "Portone 웹훅 데이터", required = true)
            @RequestBody PaymentWebhookDto webhookDto
    ) {
        log.info("웹훅 수신: {}", webhookDto);

        try {
            PaymentCallbackReqDto callbackDto = new PaymentCallbackReqDto(
                    webhookDto.getImpUid(),
                    webhookDto.getMerchantUid()
            );
            paymentService.processPayment(callbackDto);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("웹훅 처리 중 오류 발생: ", e);
            throw new CustomException(ErrorCode.PAYMENT_WEBHOOK_ERROR, e.getMessage());
        }
    }
}
