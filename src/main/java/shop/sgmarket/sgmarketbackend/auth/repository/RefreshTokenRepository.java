package shop.sgmarket.sgmarketbackend.auth.repository;

import org.springframework.data.repository.CrudRepository;
import shop.sgmarket.sgmarketbackend.auth.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
