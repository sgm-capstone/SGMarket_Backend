package shop.sgmarket.sgmarketbackend.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
