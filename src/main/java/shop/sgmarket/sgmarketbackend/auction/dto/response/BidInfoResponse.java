package shop.sgmarket.sgmarketbackend.auction.dto.response;

import lombok.Builder;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Builder
public record BidInfoResponse(
        long bidPrice,
        MemberInfo memberInfo
) {
    public static BidInfoResponse of(long bidPrice, Member member) {
        return BidInfoResponse.builder()
                .bidPrice(bidPrice)
                .memberInfo(MemberInfo.from(member))
                .build();
    }

    @Builder
    public record MemberInfo(
            Long memberId,
            String memberName
    ) {
        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .memberId(member.getId())
                    .memberName(member.getNickname())
                    .build();
        }
    }
}
