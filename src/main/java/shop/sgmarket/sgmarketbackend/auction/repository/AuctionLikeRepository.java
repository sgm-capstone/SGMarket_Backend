package shop.sgmarket.sgmarketbackend.auction.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionLike;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface AuctionLikeRepository extends JpaRepository<AuctionLike, Long> {
    Optional<AuctionLike> findByAuctionAndMember(Auction auction, Member member);
}
