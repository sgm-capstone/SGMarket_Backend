package shop.sgmarket.sgmarketbackend.member.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChargeCoinRequest(
        @NotNull(message = "충전 금액은 반드시 입력해야 합니다.")
        @Positive(message = "충전 금액은 0보다 큰 값이어야 합니다.")
        Long price
) {
}
