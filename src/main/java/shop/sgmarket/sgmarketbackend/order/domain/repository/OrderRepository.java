package shop.sgmarket.sgmarketbackend.order.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import shop.sgmarket.sgmarketbackend.order.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberId(Long memberId);

    @Query("""
        select o from Order o
        left join fetch o.payment p
        left join fetch o.member  m
        where o.orderUid = :orderUid
    """)
    Optional<Order> findOrderAndPaymentAndMember(String orderUid);

    @Query("""
        select o from Order o
        left join fetch o.payment p
        where o.orderUid = :orderUid
    """)
    Optional<Order> findOrderAndPayment(String orderUid);
}

