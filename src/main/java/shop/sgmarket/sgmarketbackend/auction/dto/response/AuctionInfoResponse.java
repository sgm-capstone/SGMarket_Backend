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
        long auctionEndPrice,
        String auctionImageUrl,
        String auctionCategory,
        ItemInfo auctionItem,
        MemberInfo auctionMember,
        String status
) {
    public static AuctionInfoResponse of(Auction auction, Item item, Member member) {
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
                .auctionItem(ItemInfo.from(item))
                .auctionMember(MemberInfo.from(member))
                .status(auction.getStatus().name())
                .build();
    }

    public record ItemInfo(
            String itemName
    ) {
        public static ItemInfo from(Item item) {
            return new ItemInfo(item.getName());
        }
    }

    public record MemberInfo(
            Long memberId,
            String memberName,
            String memberProfileImageUrl
    ) {
        public static MemberInfo from(Member member) {
            return new MemberInfo(member.getId(), member.getNickname(),
                    member.getOauthInfo().getOauthProfileImageUrl());
        }
    }
}
