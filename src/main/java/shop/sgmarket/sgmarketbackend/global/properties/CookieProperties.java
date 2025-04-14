package shop.sgmarket.sgmarketbackend.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.cookie")
public record CookieProperties(
        boolean isSecure
) {
}
