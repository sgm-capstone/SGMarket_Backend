package shop.sgmarket.sgmarketbackend.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frontend")
public record RedirectUriProperties(
        String redirectUri
) {
}
