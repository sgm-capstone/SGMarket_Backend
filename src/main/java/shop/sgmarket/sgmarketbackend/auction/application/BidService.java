package shop.sgmarket.sgmarketbackend.auction.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionStatus;
import shop.sgmarket.sgmarketbackend.auction.domain.Bid;
import shop.sgmarket.sgmarketbackend.auction.domain.PriceHistory;
import shop.sgmarket.sgmarketbackend.auction.dto.request.BidRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.BidInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.repository.auction.AuctionRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.bid.BidRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.pricehistory.PriceHistoryRepository;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.notification.application.NotificationService;
import shop.sgmarket.sgmarketbackend.notification.domain.NotificationEventType;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {

    private static final String BID_NOTIFICATION_MESSAGE = "새로운 입찰이 등록되었습니다: %s (입찰가: %d)";
    private static final String AUCTION_SETTLED_NOTIFICATION_MESSAGE = "경매가 낙찰되었습니다: %s (낙찰가: %d)";
    private static final String AUCTION_FAILED_NOTIFICATION_MESSAGE = "경매가 종료되었지만 입찰이 없어 실패로 처리되었습니다: %s";
    private static final String WINNER_NOTIFICATION_MESSAGE = "입찰한 경매 \"%s\"에 낙찰되었습니다: %d";

    private final NotificationService notificationService;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final MemberUtil memberUtil;
    private final PriceHistoryRepository priceHistoryRepository;

    @Transactional
    public BidInfoResponse bid(Long auctionId, BidRegisterRequest bidRequest) {
        Member bidder = memberUtil.getCurrentMember();
        Auction auction = getAuctionOrThrow(auctionId);

        validateBidderHasEnoughCoins(bidder, bidRequest.bidPrice());
        validateBiddingStatus(auction);
        validateNotOwner(bidder, auction);

        auction.updateCurrentPrice(bidRequest.bidPrice());
        Bid bid = Bid.createBid(bidder, auction, bidRequest.bidPrice());

        bidder.deductCoin(bidRequest.bidPrice());
        bidRepository.save(bid);
        sendBidNotification(auction, bidRequest);

        return BidInfoResponse.of(bid);
    }

    @Transactional(readOnly = true)
    public SliceResponse<BidInfoResponse> getBidsForAuction(Long auctionId, Pageable pageable) {
        Auction auction = getAuctionOrThrow(auctionId);

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Slice<Bid> bids = bidRepository.findAllByAuction(auction, sortedPageable);
        return SliceResponse.from(bids.map(BidInfoResponse::of));
    }

    @Transactional
    public BidInfoResponse settleBid(Long auctionId) {
        Member owner = memberUtil.getCurrentMember();
        Auction auction = getAuctionOrThrow(auctionId);

        validateBiddingStatus(auction);
        validateAuctionOwner(owner, auction);

        Bid winningBid = bidRepository.findTopByAuctionOrderByCreatedAtDescPriceDesc(auction)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));

        auction.updateStatus(AuctionStatus.COMPLETED);
        PriceHistory priceHistory = PriceHistory.createPriceHistory(
                auction.getItem(),
                winningBid.getPrice()
        );
        priceHistoryRepository.save(priceHistory);

        Long winningBidId = winningBid.getId();
        List<Object[]> refundData = bidRepository.findRefundAmountsByAuctionExceptWinning(auctionId, winningBidId);

        for (Object[] row : refundData) {
            Long memberId    = (Long) row[0];
            Long refundAmount = (Long) row[1];

            Member bidderMember = memberUtil.getMemberByMemberId(memberId);
            bidderMember.chargeCoin(refundAmount);
        }
        notifyAuctionSettled(auction, winningBid);

        return BidInfoResponse.of(winningBid);
    }

    @Transactional(readOnly = true)
    public BidInfoResponse getMaxBidForAuction(Long auctionId) {
        Auction auction = getAuctionOrThrow(auctionId);

        Bid maxBid = bidRepository.findTopByAuctionOrderByPriceDesc(auction)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));

        return BidInfoResponse.of(maxBid);
    }

    @Transactional
    public void closeExpiredAuctions() {
        List<Auction> expiredAuctions = auctionRepository.findAllByEndDateBeforeAndStatus(
                LocalDateTime.now(), AuctionStatus.BIDDING);

        for (Auction auction : expiredAuctions) {
            try {
                closeAuction(auction);
            } catch (Exception e) {
                log.error("[closeExpiredAuctions] auctionId={} 처리 중 오류 발생: {}", auction.getId(), e.getMessage(), e);
            }
        }
    }

    private void closeAuction(Auction auction) {
        Optional<Bid> optionalHighestBid = bidRepository.findTopByAuctionOrderByPriceDesc(auction);

        if (optionalHighestBid.isEmpty()) {
            auction.updateStatus(AuctionStatus.FAILED);
            notifyAuctionFailed(auction);
            return;
        }

        Bid highestBid = optionalHighestBid.get();
        auction.updateStatus(AuctionStatus.COMPLETED);

        PriceHistory priceHistory = PriceHistory.createPriceHistory(
                auction.getItem(),
                highestBid.getPrice()
        );
        priceHistoryRepository.save(priceHistory);

        notifyAuctionSettled(auction, highestBid);
    }

    private void sendBidNotification(Auction auction, BidRegisterRequest bidRequest) {
        String message = String.format(
                BID_NOTIFICATION_MESSAGE,
                auction.getTitle(),
                bidRequest.bidPrice()
        );
        notificationService.createAndSendNotification(
                auction.getMember(),
                NotificationEventType.BID,
                message
        );
    }

    private void notifyAuctionSettled(Auction auction, Bid winningBid) {
        String title = auction.getTitle();
        long price = winningBid.getPrice();

        String toWinnerMsg = String.format(
                WINNER_NOTIFICATION_MESSAGE,
                title,
                price
        );
        notificationService.createAndSendNotification(
                winningBid.getMember(),
                NotificationEventType.AUCTION_SETTLED,
                toWinnerMsg
        );

        String toOwnerMsg = String.format(
                AUCTION_SETTLED_NOTIFICATION_MESSAGE,
                title,
                price
        );
        notificationService.createAndSendNotification(
                auction.getMember(),
                NotificationEventType.AUCTION_SETTLED,
                toOwnerMsg
        );
    }

    private void notifyAuctionFailed(Auction auction) {
        String title = auction.getTitle();

        String message = String.format(
                AUCTION_FAILED_NOTIFICATION_MESSAGE,
                title
        );
        notificationService.createAndSendNotification(
                auction.getMember(),
                NotificationEventType.AUCTION_FAILED,
                message
        );
    }

    private Auction getAuctionOrThrow(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
    }

    private void validateBidderHasEnoughCoins(Member bidder, long bidPrice) {
        if (bidder.getCoin() < bidPrice) {
            throw new CustomException(ErrorCode.INSUFFICIENT_COINS);
        }
    }

    private void validateBiddingStatus(Auction auction) {
        if (auction.getStatus() != AuctionStatus.BIDDING) {
            throw new CustomException(ErrorCode.AUCTION_NOT_BIDDING);
        }
    }

    private void validateNotOwner(Member bidder, Auction auction) {
        if (Objects.equals(bidder.getId(), auction.getMember().getId())) {
            throw new CustomException(ErrorCode.CANNOT_BID_OWN_AUCTION);
        }
    }

    private void validateAuctionOwner(Member member, Auction auction) {
        if (!Objects.equals(member.getId(), auction.getMember().getId())) {
            throw new CustomException(ErrorCode.NOT_AUCTION_OWNER);
        }
    }
}
