package shop.sgmarket.sgmarketbackend.auction.repository.pricehistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import shop.sgmarket.sgmarketbackend.auction.domain.QPriceHistory;
import shop.sgmarket.sgmarketbackend.auction.domain.PriceHistory;
import shop.sgmarket.sgmarketbackend.auction.dto.response.PriceHistoryInfoResponse;

import java.util.List;

@RequiredArgsConstructor
public class PriceHistoryRepositoryImpl implements PriceHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPriceHistory priceHistory = QPriceHistory.priceHistory;

    @Override
    public List<PriceHistoryInfoResponse> findPriceHistoryInfoByItemId(Long itemId) {
        List<PriceHistory> histories = queryFactory
                .selectFrom(priceHistory)
                .where(priceHistory.item.id.eq(itemId))
                .orderBy(priceHistory.recordedAt.asc())
                .fetch();

        return histories.stream()
                .map(PriceHistoryInfoResponse::from)
                .toList();
    }
}
