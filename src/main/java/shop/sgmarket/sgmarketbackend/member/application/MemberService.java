package shop.sgmarket.sgmarketbackend.member.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.bid.BidRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.auctionLike.AuctionLikeRepository;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.domain.MemberLocation;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberInfoResponse;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberUtil memberUtil;
    private final AuctionRepository auctionRepository;
    private final AuctionLikeRepository auctionLikeRepository;
    private final BidRepository bidRepository;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfoFromId() {
        Member member = memberUtil.getCurrentMember();

        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateProfile(final MemberUpdateRequest memberUpdateRequest) {
        Member member = memberUtil.getCurrentMember();
        MemberLocation memberLocation = MemberLocation.createMemberLocation(
                memberUpdateRequest.address(),
                memberUpdateRequest.latitude(),
                memberUpdateRequest.longitude()
        );

        member.updateProfile(memberLocation, memberUpdateRequest.nickname());

        return MemberInfoResponse.from(member);
    }

    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getMyAuctions(Pageable pageable) {
        Member member = memberUtil.getCurrentMember();
        Slice<Auction> myAuctions = auctionRepository.findByMember(member, pageable);

        List<Long> auctionIds = myAuctions.stream()
                .map(Auction::getId)
                .toList();

        List<Long> likedAuctionIds = auctionLikeRepository.findAuctionIdsByMemberAndAuctionIds(member, auctionIds);

        Set<Long> likedAuctionIdSet = new HashSet<>(likedAuctionIds);

        Slice<AuctionInfoResponse> auctionInfoResponses = myAuctions.map(otherAuction -> {
            boolean isLiked = likedAuctionIdSet.contains(otherAuction.getId());
            return AuctionInfoResponse.of(otherAuction, otherAuction.getItem(), member, isLiked);
        });

        return SliceResponse.from(auctionInfoResponses);
    }

    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getMyLikedAuctions(Pageable pageable) {
        Member member = memberUtil.getCurrentMember();
        Slice<Auction> auctionSlice = auctionLikeRepository.findAuctionsByMember(member, pageable);
        Slice<AuctionInfoResponse> responseSlice = auctionSlice.map(auction ->
                AuctionInfoResponse.of(auction, auction.getItem(), member, true)
        );
        return SliceResponse.from(responseSlice);
    }

    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getMyBiddedAuctions(Pageable pageable) {
        Member member = memberUtil.getCurrentMember();

        Slice<Auction> auctionSlice =
                bidRepository.findAuctionsByMember(member, pageable);

        Slice<AuctionInfoResponse> responseSlice = auctionSlice.map(auction ->
                AuctionInfoResponse.of(
                        auction,
                        auction.getItem(),
                        member,
                        auctionLikeRepository.existsByAuctionAndMember(auction, member)
                )
        );

        return SliceResponse.from(responseSlice);
    }


}
