package shop.sgmarket.sgmarketbackend.auction.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.sgmarket.sgmarketbackend.auction.application.BidService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final BidService bidService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void closeExpiredAuctions() {
        log.info("[스케줄러] 마감된 경매 자동 낙찰 처리 시작");
        bidService.closeExpiredAuctions();
    }
}
