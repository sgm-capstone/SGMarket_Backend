package shop.sgmarket.sgmarketbackend.auction.repository.auction;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionStatus;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface AuctionRepositoryCustom {

    Slice<AuctionInfoResponse> findAuctionsWithinRadius(
            double lat,
            double lng,
            double radiusKm,
            AuctionCategory category,
            Pageable pageable,
            Member viewer
    );

    Optional<Long> findItemIdByAuctionId(Long auctionId);

    Optional<AuctionInfoResponse> findAuctionInfoByIdAndStatus(
            Long auctionId,
            AuctionStatus status,
            Member viewer
    );

    Slice<AuctionInfoResponse> findAuctionInfoByMemberExcept(
            Member author,
            Long excludedAuctionId,
            AuctionStatus status,
            Pageable pageable,
            Member viewer
    );

    Optional<AuctionInfoResponse> findRandomAuctionInfoByStatus(
            AuctionStatus status,
            Member viewer
    );
}
