package shop.sgmarket.sgmarketbackend.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauthInfoOauthProviderAndOauthInfoOauthId(String oauthProvider, String oauthId);
}
