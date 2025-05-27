package shop.sgmarket.sgmarketbackend.auction.repository.auctionLike;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
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

    @Override
    public Slice<Auction> findAuctionsByMember(Member member, Pageable pageable) {
        List<Auction> content = queryFactory
                .select(auctionLike.auction)
                .from(auctionLike)
                .where(auctionLike.member.eq(member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.removeLast();
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
