package shop.sgmarket.sgmarketbackend.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
public record GoogleProperties(
        String clientId,
        String clientSecret,
        String redirectUri
) {
}
