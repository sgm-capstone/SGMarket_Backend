package shop.sgmarket.sgmarketbackend.auction.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.Item;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Getter
@Builder
public class AuctionInfoResponse {
    private final Long auctionId;
    private final String auctionTitle;
    private final String auctionDescription;
    private final String auctionStartDate;
    private final String auctionEndDate;
    private final long auctionStartPrice;
    private final long auctionCurrentPrice;
    private final Long auctionEndPrice;
    private final String auctionImageUrl;
    private final String auctionCategory;
    private final boolean isLiked;
    private final long likeCount;
    private final String itemName;
    private final Long memberId;
    private final String memberName;
    private final String memberProfileImageUrl;
    private final String status;

    @QueryProjection
    public AuctionInfoResponse(
            Long auctionId,
            String auctionTitle,
            String auctionDescription,
            String auctionStartDate,
            String auctionEndDate,
            long auctionStartPrice,
            long auctionCurrentPrice,
            Long auctionEndPrice,
            String auctionImageUrl,
            String auctionCategory,
            boolean isLiked,
            long likeCount,
            String itemName,
            Long memberId,
            String memberName,
            String memberProfileImageUrl,
            String status
    ) {
        this.auctionId = auctionId;
        this.auctionTitle = auctionTitle;
        this.auctionDescription = auctionDescription;
        this.auctionStartDate = auctionStartDate;
        this.auctionEndDate = auctionEndDate;
        this.auctionStartPrice = auctionStartPrice;
        this.auctionCurrentPrice = auctionCurrentPrice;
        this.auctionEndPrice = auctionEndPrice;
        this.auctionImageUrl = auctionImageUrl;
        this.auctionCategory = auctionCategory;
        this.isLiked = isLiked;
        this.likeCount = likeCount;
        this.itemName = itemName;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberProfileImageUrl = memberProfileImageUrl;
        this.status = status;
    }

    public static AuctionInfoResponse of(Auction auction, Item item, Member member, boolean isLiked) {
        return AuctionInfoResponse.builder()
                .auctionId(auction.getId())
                .auctionTitle(auction.getTitle())
                .auctionDescription(auction.getDescription())
                .auctionStartDate(auction.getStartDate().toString())
                .auctionEndDate(auction.getEndDate().toString())
                .auctionStartPrice(auction.getStartPrice())
                .auctionCurrentPrice(auction.getCurrentPrice())
                .auctionEndPrice(auction.getEndPrice())
                .auctionImageUrl(auction.getImageUrl())
                .auctionCategory(auction.getCategory().name())
                .isLiked(isLiked)
                .likeCount(auction.getLikeCount())
                .itemName(item.getName())
                .memberId(member.getId())
                .memberName(member.getNickname())
                .memberProfileImageUrl(member.getOauthInfo().getOauthProfileImageUrl())
                .status(auction.getStatus().name())
                .build();
    }
}
