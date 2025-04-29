package shop.sgmarket.sgmarketbackend.global.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "storage")
public record S3Properties(
        @NotNull String accessKey,
        @NotNull String secretKey,
        @NotNull String bucket,
        @NotNull String region
) {
}


