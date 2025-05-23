package shop.sgmarket.sgmarketbackend.payment.domain.repository;

import shop.sgmarket.sgmarketbackend.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
