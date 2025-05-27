package shop.sgmarket.sgmarketbackend.auction.repository.bid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface BidRepositoryCustom {
    Slice<Auction> findAuctionsByMember(Member member, Pageable pageable);
}
