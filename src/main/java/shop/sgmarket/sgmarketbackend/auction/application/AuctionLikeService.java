package shop.sgmarket.sgmarketbackend.auction.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionLike;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionToggleLikeResponse;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionLikeRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionRepository;
import shop.sgmarket.sgmarketbackend.global.domain.Status;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Service
@RequiredArgsConstructor
public class AuctionLikeService {

    private final AuctionLikeRepository auctionLikeRepository;
    private final AuctionRepository auctionRepository;

    private final MemberUtil memberUtil;

    @Transactional
    public AuctionToggleLikeResponse toggleLike(Long auctionId) {
        Auction auction = auctionRepository.findByIdAndStatus(auctionId, Status.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        Member member = memberUtil.getCurrentMember();
        boolean isLiked = updateLikeStatusForMember(member, auction);

        return AuctionToggleLikeResponse.of(auctionId, isLiked, auction.getLikeCount());
    }

    private boolean updateLikeStatusForMember(Member member, Auction auction) {
        Optional<AuctionLike> existingLike = auctionLikeRepository.findByAuctionAndMember(auction, member);

        if (existingLike.isPresent()) {
            auctionLikeRepository.delete(existingLike.get());
            auction.decrementLikeCount();
            return false;
        }

        auctionLikeRepository.save(AuctionLike.createAuctionLike(member, auction));
        auction.incrementLikeCount();
        return true;
    }
}
