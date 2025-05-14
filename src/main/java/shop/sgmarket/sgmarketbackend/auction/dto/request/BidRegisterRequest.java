package shop.sgmarket.sgmarketbackend.auction.dto.request;

import jakarta.validation.constraints.Positive;

public record BidRegisterRequest(
        @Positive(message = "입찰가는 양수여야 합니다")
        long bidPrice
) {
}
