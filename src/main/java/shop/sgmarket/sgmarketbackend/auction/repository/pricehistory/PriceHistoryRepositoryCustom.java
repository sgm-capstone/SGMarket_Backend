package shop.sgmarket.sgmarketbackend.auction.repository.pricehistory;

import java.util.List;
import shop.sgmarket.sgmarketbackend.auction.dto.response.PriceHistoryInfoResponse;

public interface PriceHistoryRepositoryCustom {
    List<PriceHistoryInfoResponse> findPriceHistoryInfoByItemId(Long itemId);
}
