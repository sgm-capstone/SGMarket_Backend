package shop.sgmarket.sgmarketbackend.auction.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import shop.sgmarket.sgmarketbackend.auction.domain.Bid;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Builder
public record BidInfoResponse(
        long bidPrice,
        MemberInfo memberInfo,
        LocalDateTime bidTime
) {
    public static BidInfoResponse of(Bid bid) {
        return BidInfoResponse.builder()
                .bidPrice(bid.getPrice())
                .memberInfo(MemberInfo.from(bid.getMember()))
                .bidTime(bid.getCreatedAt())
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
