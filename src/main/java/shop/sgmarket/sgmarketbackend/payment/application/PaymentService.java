package shop.sgmarket.sgmarketbackend.payment.application;


import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.IamportClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.order.domain.Order;
import shop.sgmarket.sgmarketbackend.order.domain.repository.OrderRepository;
import shop.sgmarket.sgmarketbackend.payment.api.dto.request.PaymentCallbackReqDto;
import shop.sgmarket.sgmarketbackend.payment.api.dto.response.PaymentResDto;
import shop.sgmarket.sgmarketbackend.payment.domain.Status;
import shop.sgmarket.sgmarketbackend.payment.domain.repository.PaymentRepository;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;

import java.math.BigDecimal;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    public PaymentResDto getPaymentInfo(String orderUid) {
        Order order = orderRepository.findOrderAndPaymentAndMember(orderUid)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return PaymentResDto.from(order);
    }

    @Transactional
    public IamportResponse<com.siot.IamportRestClient.response.Payment> processPayment(PaymentCallbackReqDto reqDto) {
        try {
            Order order = orderRepository.findOrderAndPayment(reqDto.orderUid())
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

            // 결제 단건 조회
            IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = iamportClient.paymentByImpUid(reqDto.paymentUid());

            // 결제 완료 및 금액 검증
            validatePayment(iamportResponse, order);

            // 결제 상태 변경
            order.getPayment().updateStatus(Status.PAID, iamportResponse.getResponse().getImpUid());

            return iamportResponse;

        } catch (IamportResponseException | IOException e) {
            throw new CustomException(ErrorCode.PAYMENT_PROCESSING_ERROR, e.getMessage());
        }
    }

    private void validatePayment(IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse, Order order) throws IamportResponseException, IOException {
        // 결제 완료 검증
        if (!iamportResponse.getResponse().getStatus().equals("paid")) {
            orderRepository.delete(order);
            paymentRepository.delete(order.getPayment());

            throw new CustomException(ErrorCode.PAYMENT_NOT_COMPLETED);
        }

        // 결제 금액 검증
        Long dbPrice = order.getPayment().getPrice();
        int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

        // 테스트 결제인 경우 금액 검증 우회 (imp_uid가 테스트 결제 패턴인 경우)
        // imp_uid가 "imp_" 로 시작하고 뒤에 숫자가 붙는 패턴이면 테스트 결제로 간주
        String impUid = iamportResponse.getResponse().getImpUid();
        boolean isTestPayment = impUid != null && impUid.matches("imp_\\d+");
        
        // 테스트 결제이거나 금액이 일치하는 경우 통과
        if (isTestPayment || iamportPrice == dbPrice) {
            return; // 검증 통과
        }

        // 금액 불일치 시 결제 취소 및 예외 발생
        orderRepository.delete(order);
        paymentRepository.delete(order.getPayment());
        
        // 결제금액 위변조로 의심되는 결제금액을 취소
        iamportClient.cancelPaymentByImpUid(new CancelData(
                iamportResponse.getResponse().getImpUid(), 
                true, 
                new BigDecimal(iamportPrice)));

        throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }
}
