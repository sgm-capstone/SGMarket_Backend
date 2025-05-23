package shop.sgmarket.sgmarketbackend.auction.repository.auctionLike;

import java.util.List;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface AuctionLikeRepositoryCustom {
    List<Long> findAuctionIdsByMemberAndAuctionIds(Member member, List<Long> auctionIds);
}
