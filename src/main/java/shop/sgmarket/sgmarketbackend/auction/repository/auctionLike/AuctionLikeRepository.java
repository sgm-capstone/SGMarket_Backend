package shop.sgmarket.sgmarketbackend.auction.repository.auctionLike;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionLike;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface AuctionLikeRepository extends JpaRepository<AuctionLike, Long>, AuctionLikeRepositoryCustom {
    Optional<AuctionLike> findByAuctionAndMember(Auction auction, Member member);
    boolean existsByAuctionAndMember(Auction auction, Member member);
    @Query("""
        SELECT a.auction
        FROM AuctionLike a
        WHERE a.member = :member
    """)
    Slice<Auction> findAuctionsByMember(@Param("member") Member member, Pageable pageable);
}
