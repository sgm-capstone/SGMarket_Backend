package shop.sgmarket.sgmarketbackend.auction.dto.response;

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
                .likeCount(auction.getLikeCount())
                .isLiked(isLiked)
                .auctionItem(AuctionInfoResponse.ItemInfo.from(item))
                .auctionMember(MemberInfo.from(member))
                .status(auction.getStatus().name())
                .build();
    }

    @Builder
    public record ItemInfo(
            String itemName
    ) {
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
            String memberProfileImageUrl
    ) {
        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .memberId(member.getId())
                    .memberName(member.getNickname())
                    .memberProfileImageUrl(member.getOauthInfo().getOauthProfileImageUrl())
                    .build();
        }
    }
}
