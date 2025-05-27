package shop.sgmarket.sgmarketbackend.auction.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.bid.BidRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.PriceHistoryRepository;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Service
@RequiredArgsConstructor
public class BidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final MemberUtil memberUtil;
    private final PriceHistoryRepository priceHistoryRepository;

    @Transactional
    public BidInfoResponse bid(Long auctionId, BidRegisterRequest bidRequest) {
        Member bidder = memberUtil.getCurrentMember();
        Auction auction = getAuctionOrThrow(auctionId);

        validateBiddingStatus(auction);
        validateNotOwner(bidder, auction);

        auction.updateCurrentPrice(bidRequest.bidPrice());
        Bid bid = Bid.createBid(bidder, auction, bidRequest.bidPrice());

        bidRepository.save(bid);

        return BidInfoResponse.of(bid.getPrice(), bidder);
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

        return SliceResponse.from(bids.map(bid -> BidInfoResponse.of(bid.getPrice(), bid.getMember())));
    }

    @Transactional
    public BidInfoResponse settleBid(Long auctionId) {
        Member owner = memberUtil.getCurrentMember();
        Auction auction = getAuctionOrThrow(auctionId);

        validateBiddingStatus(auction);
        validateAuctionOwner(owner, auction);

        Bid winningBid = bidRepository.findTopByAuctionOrderByCreatedAtDesc(auction)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));

        auction.updateStatus(AuctionStatus.COMPLETED);
        PriceHistory priceHistory = PriceHistory.createPriceHistory(
                auction.getItem(),
                winningBid.getPrice()
        );
        priceHistoryRepository.save(priceHistory);

        return BidInfoResponse.of(winningBid.getPrice(), winningBid.getMember());
    }

    @Transactional(readOnly = true)
    public BidInfoResponse getMaxBidForAuction(Long auctionId) {
        Auction auction = getAuctionOrThrow(auctionId);

        Bid maxBid = bidRepository.findTopByAuctionOrderByPriceDesc(auction)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));

        return BidInfoResponse.of(maxBid.getPrice(), maxBid.getMember());
    }

    private Auction getAuctionOrThrow(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
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

    @Transactional
    public void closeExpiredAuctions() {
        List<Auction> expiredAuctions = auctionRepository.findAllByEndDateBeforeAndStatus(
                LocalDateTime.now(), AuctionStatus.BIDDING);

        for (Auction auction : expiredAuctions) {
            closeAuction(auction);
        }
    }

    private void closeAuction(Auction auction) {
        Optional<Bid> optionalHighestBid = bidRepository.findTopByAuctionOrderByPriceDesc(auction);

        if (optionalHighestBid.isEmpty()) {
            auction.updateStatus(AuctionStatus.FAILED);
            return;
        }

        Bid highestBid = optionalHighestBid.get();
        auction.updateStatus(AuctionStatus.COMPLETED);

        PriceHistory priceHistory = PriceHistory.createPriceHistory(
                auction.getItem(),
                highestBid.getPrice());
        priceHistoryRepository.save(priceHistory);
    }

}
