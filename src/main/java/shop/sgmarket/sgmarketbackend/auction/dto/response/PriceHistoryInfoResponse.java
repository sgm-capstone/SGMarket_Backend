package shop.sgmarket.sgmarketbackend.auction.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import shop.sgmarket.sgmarketbackend.auction.domain.PriceHistory;

@Builder
public record PriceHistoryInfoResponse(
        Long id,
        long price,
        LocalDateTime recordedAt
) {

    @QueryProjection
    public PriceHistoryInfoResponse{};

    public static PriceHistoryInfoResponse from(PriceHistory priceHistory) {
        return PriceHistoryInfoResponse.builder()
                .id(priceHistory.getId())
                .price(priceHistory.getPrice())
                .recordedAt(priceHistory.getRecordedAt())
                .build();
    }
}
