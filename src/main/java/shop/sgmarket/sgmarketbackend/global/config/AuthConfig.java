package shop.sgmarket.sgmarketbackend.global.config;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.sgmarket.sgmarketbackend.auth.domain.OAuthProvider;
import shop.sgmarket.sgmarketbackend.auth.application.OAuthClient;

@Configuration
@RequiredArgsConstructor
public class AuthConfig {

    private final Map<String, OAuthClient> oAuthClients;

    @Bean
    public Map<OAuthProvider, OAuthClient> oAuthClientMap() {
        return oAuthClients.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> OAuthProvider.from(entry.getKey()),
                        Map.Entry::getValue
                ));
    }
}
