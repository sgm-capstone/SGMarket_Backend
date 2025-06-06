package shop.sgmarket.sgmarketbackend.auction.repository.bid;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.QBid;
import shop.sgmarket.sgmarketbackend.auction.dto.RefundAmount;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@RequiredArgsConstructor
public class BidRepositoryImpl implements BidRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QBid bid = QBid.bid;

    @Override
    public Slice<Auction> findAuctionsByMember(Member member, Pageable pageable) {
        List<Auction> content = queryFactory
                .select(bid.auction).distinct()
                .from(bid)
                .where(bid.member.eq(member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.removeLast();
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<RefundAmount> findRefundAmountsByAuctionExceptWinning(Long auctionId, Long winningBidId) {
        List<Tuple> tuples = queryFactory
                .select(bid.member.id, bid.price.sum())
                .from(bid)
                .where(
                        bid.auction.id.eq(auctionId)
                                .and(bid.id.ne(winningBidId))
                )
                .groupBy(bid.member.id)
                .fetch();

        return tuples.stream()
                .map(t -> new RefundAmount(
                        t.get(bid.member.id),
                        t.get(bid.price.sum())
                ))
                .collect(Collectors.toList());
    }
}
