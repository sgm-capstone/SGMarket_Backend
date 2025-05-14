package shop.sgmarket.sgmarketbackend.auction.application;

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
import shop.sgmarket.sgmarketbackend.auction.dto.request.BidRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.BidInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.BidRepository;
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

    @Transactional
    public BidInfoResponse bid(Long auctionId, BidRegisterRequest bidRegisterRequest) {
        Member member = memberUtil.getCurrentMember();
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
        if (auction.getStatus() != AuctionStatus.BIDDING) {
            throw new CustomException(ErrorCode.AUCTION_NOT_BIDDING);
        }

        if (member == auction.getMember()) {
            throw new CustomException(ErrorCode.CANNOT_BID_OWN_AUCTION);
        }

        auction.updateCurrentPrice(bidRegisterRequest.bidPrice());
        Bid bid = Bid.createBid(member, auction, bidRegisterRequest.bidPrice());

        bidRepository.save(bid);

        return BidInfoResponse.of(bidRegisterRequest.bidPrice(), member);
    }

    @Transactional(readOnly = true)
    public SliceResponse<BidInfoResponse> getBidsForAuction(Long auctionId, Pageable pageable) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Slice<Bid> bids = bidRepository.findAllByAuction(auction, sortedPageable);

        return SliceResponse.from(
                bids.map(bid -> BidInfoResponse.of(bid.getPrice(), bid.getMember()))
        );
    }

}
