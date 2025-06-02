package shop.sgmarket.sgmarketbackend.auction.repository.auction;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static shop.sgmarket.sgmarketbackend.auction.domain.QItem.item;
import static shop.sgmarket.sgmarketbackend.member.domain.QMember.member;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionStatus;
import shop.sgmarket.sgmarketbackend.auction.domain.QAuction;
import shop.sgmarket.sgmarketbackend.auction.domain.QAuctionLike;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.QAuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.QAuctionInfoResponse_ItemInfo;
import shop.sgmarket.sgmarketbackend.auction.dto.response.QAuctionInfoResponse_MemberInfo;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@RequiredArgsConstructor
public class AuctionRepositoryImpl implements AuctionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QAuction auction = QAuction.auction;
    private final QAuctionLike auctionLike = QAuctionLike.auctionLike;


    @Override
    public Slice<AuctionInfoResponse> findAuctionsWithinRadius(
            double lat,
            double lng,
            double radiusKm,
            AuctionCategory category,
            Pageable pageable,
            Member viewer
    ) {
        NumberExpression<Double> distance = numberTemplate(
                Double.class,
                "{0} * acos(cos(radians({1})) * cos(radians({2})) * cos(radians({3}) - radians({4})) + sin(radians({1})) * sin(radians({2})))",
                6371, lat, auction.latitude, auction.longitude, lng
        );

        JPAQuery<AuctionInfoResponse> query = baseResponse(viewer)
                .where(
                        auction.status.eq(AuctionStatus.BIDDING),
                        category != null ? auction.category.eq(category) : null,
                        distance.loe(radiusKm)
                );

        return fetchSlice(query, pageable);
    }

    @Override
    public Optional<Long> findItemIdByAuctionId(Long auctionId) {
        Long itemId = queryFactory
                .select(auction.item.id)
                .from(auction)
                .where(auction.id.eq(auctionId))
                .fetchOne();

        return Optional.ofNullable(itemId);
    }

    @Override
    public Optional<AuctionInfoResponse> findAuctionInfoByIdAndStatus(
            Long auctionId,
            AuctionStatus status,
            Member viewer
    ) {
        AuctionInfoResponse dto = baseResponse(viewer)
                .where(
                        auction.id.eq(auctionId),
                        auction.status.eq(status)
                )
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    @Override
    public Slice<AuctionInfoResponse> findAuctionInfoByMemberExcept(
            Member author,
            Long excludedAuctionId,
            AuctionStatus status,
            Pageable pageable,
            Member viewer
    ) {
        JPAQuery<AuctionInfoResponse> query = baseResponse(viewer)
                .where(
                        auction.member.eq(author),
                        auction.id.ne(excludedAuctionId),
                        auction.status.eq(status)
                );

        return fetchSlice(query, pageable);
    }

    @Override
    public Optional<AuctionInfoResponse> findRandomAuctionInfoByStatus(
            AuctionStatus status,
            Member viewer
    ) {
        Long count = queryFactory
                .select(auction.count())
                .from(auction)
                .where(auction.status.eq(status))
                .fetchOne();

        if (count == null || count == 0) {
            return Optional.empty();
        }
        int offset = (int) (Math.random() * count);

        AuctionInfoResponse dto = baseResponse(viewer)
                .where(auction.status.eq(status))
                .offset(offset)
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    private JPAQuery<AuctionInfoResponse> baseResponse(Member viewer) {
        return queryFactory
                .select(new QAuctionInfoResponse(
                        auction.id,
                        auction.title,
                        auction.description,
                        auction.startDate.stringValue(),
                        auction.endDate.stringValue(),
                        auction.startPrice,
                        auction.currentPrice,
                        auction.endPrice,
                        auction.imageUrl,
                        auction.category.stringValue(),
                        auctionLike.id.isNotNull().coalesce(false),
                        auction.likeCount,
                        new QAuctionInfoResponse_ItemInfo(
                                auction.item.name
                        ),
                        new QAuctionInfoResponse_MemberInfo(
                                auction.member.id,
                                auction.member.nickname,
                                auction.member.oauthInfo.oauthProfileImageUrl
                        ),
                        auction.status.stringValue()
                ))
                .from(auction)
                .join(auction.item, item)
                .join(auction.member, member)
                .leftJoin(auctionLike)
                .on(
                        auctionLike.auction.eq(auction)
                                .and(auctionLike.member.eq(viewer))
                );
    }


    private Slice<AuctionInfoResponse> fetchSlice(
            JPAQuery<AuctionInfoResponse> query,
            Pageable pageable
    ) {
        List<AuctionInfoResponse> content = query
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
