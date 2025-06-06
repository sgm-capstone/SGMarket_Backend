package shop.sgmarket.sgmarketbackend.auction.repository.pricehistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shop.sgmarket.sgmarketbackend.auction.domain.QPriceHistory;
import shop.sgmarket.sgmarketbackend.auction.dto.response.PriceHistoryInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.QPriceHistoryInfoResponse;

@RequiredArgsConstructor
public class PriceHistoryRepositoryImpl implements PriceHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPriceHistory priceHistory = QPriceHistory.priceHistory;

    @Override
    public List<PriceHistoryInfoResponse> findPriceHistoryInfoByItemId(Long itemId) {
        return queryFactory
                .select(new QPriceHistoryInfoResponse(
                        priceHistory.id,
                        priceHistory.price,
                        priceHistory.recordedAt
                ))
                .from(priceHistory)
                .where(priceHistory.item.id.eq(itemId))
                .orderBy(priceHistory.recordedAt.asc())
                .fetch();
    }

}
