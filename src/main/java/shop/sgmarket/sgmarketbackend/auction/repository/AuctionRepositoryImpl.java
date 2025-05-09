package shop.sgmarket.sgmarketbackend.auction.repository;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;
import shop.sgmarket.sgmarketbackend.auction.domain.QAuction;
import shop.sgmarket.sgmarketbackend.global.domain.Status;

@RequiredArgsConstructor
public class AuctionRepositoryImpl implements AuctionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Auction> findAuctionsWithinRadius(
            double lat,
            double lng,
            double radiusKm,
            Status status,
            AuctionCategory category,
            Pageable pageable
    ) {
        QAuction auction = QAuction.auction;

        List<Auction> content = queryFactory
                .selectFrom(auction)
                .where(
                        auction.status.eq(status),
                        eqCategory(auction, category),
                        haversineDistance(auction.latitude, auction.longitude, lat, lng).loe(radiusKm)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.removeLast();
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression eqCategory(QAuction auction, AuctionCategory category) {
        if (category != null) {
            return auction.category.eq(category);
        }
        return null;
    }

    private NumberExpression<Double> haversineDistance(
            NumberPath<Double> latField,
            NumberPath<Double> lngField,
            double lat, double lng
    ) {
        double earthRadius = 6371;

        return numberTemplate(Double.class,
                "{0} * acos(cos(radians({1})) * cos(radians({2})) * cos(radians({3}) - radians({4})) + sin(radians({1})) * sin(radians({2})))",
                earthRadius,
                lat,
                latField,
                lngField,
                lng
        );
    }
}
