package shop.sgmarket.sgmarketbackend.auction.repository.auctionLike;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface AuctionLikeRepositoryCustom {
    List<Long> findAuctionIdsByMemberAndAuctionIds(Member member, List<Long> auctionIds);
    Slice<Auction> findAuctionsByMember(Member member, Pageable pageable);
}
