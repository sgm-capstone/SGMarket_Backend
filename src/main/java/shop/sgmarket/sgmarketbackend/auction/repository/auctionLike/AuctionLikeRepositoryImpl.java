package shop.sgmarket.sgmarketbackend.auction.repository.auctionLike;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shop.sgmarket.sgmarketbackend.auction.domain.QAuctionLike;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@RequiredArgsConstructor
public class AuctionLikeRepositoryImpl implements AuctionLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QAuctionLike auctionLike = QAuctionLike.auctionLike;

    @Override
    public List<Long> findAuctionIdsByMemberAndAuctionIds(Member member, List<Long> auctionIds) {
        return queryFactory
                .select(auctionLike.auction.id)
                .from(auctionLike)
                .where(
                    auctionLike.member.eq(member),
                    auctionLike.auction.id.in(auctionIds)
                )
                .fetch();
    }
}
