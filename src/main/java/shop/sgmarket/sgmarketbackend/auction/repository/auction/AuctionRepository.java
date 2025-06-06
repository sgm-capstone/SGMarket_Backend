package shop.sgmarket.sgmarketbackend.auction.repository.auction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionStatus;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryCustom {
    Optional<Auction> findByIdAndStatus(Long id, AuctionStatus status);

    List<Auction> findAllByEndDateBeforeAndStatus(LocalDateTime time, AuctionStatus status);

    Slice<Auction> findByMember(Member member, Pageable pageable);
}
