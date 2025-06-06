package shop.sgmarket.sgmarketbackend.auction.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.Item;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Builder
public record AuctionInfoResponse(
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
        ItemInfo auctionItem,
        MemberInfo auctionMember,
        String status
) {

    @QueryProjection
    public AuctionInfoResponse {
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
                .auctionCategory(auction.getCategory().getName())
                .isLiked(isLiked)
                .likeCount(auction.getLikeCount())
                .auctionItem(ItemInfo.from(item))
                .auctionMember(MemberInfo.from(member))
                .status(auction.getStatus().name())
                .build();
    }

    @Builder
    public record ItemInfo(
            String itemName
    ) {
        @QueryProjection
        public ItemInfo {
        }

        public static ItemInfo from(Item item) {
            return ItemInfo.builder()
                    .itemName(item.getName())
                    .build();
        }
    }

    @Builder
    public record MemberInfo(
            Long memberId,
            String memberName,
            String memberProfileImageUrl,
            Long coin
    ) {

        @QueryProjection
        public MemberInfo {
        }

        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .memberId(member.getId())
                    .memberName(member.getNickname())
                    .memberProfileImageUrl(member.getOauthInfo().getOauthProfileImageUrl())
                    .coin(member.getCoin())
                    .build();
        }
    }
}
