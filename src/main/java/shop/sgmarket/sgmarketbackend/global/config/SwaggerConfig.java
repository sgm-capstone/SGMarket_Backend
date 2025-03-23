package shop.sgmarket.sgmarketbackend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.sgmarket.sgmarketbackend.global.properties.SwaggerProperties;

@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String JWT_SCHEME_NAME = "JWT TOKEN";

    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI sgMarketAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(server()))
                .addSecurityItem(securityRequirement())
                .components(securityComponents());
    }

    private Info apiInfo() {
        return new Info()
                .title("SGMarket API")
                .description("SGMarket API 명세서")
                .version("1.0.0");
    }

    private Server server() {
        String url = swaggerProperties.url();
        String description = url.contains("localhost") ? "Local Server" : "Dev Server";

        return new Server()
                .url(url)
                .description(description);
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(JWT_SCHEME_NAME);
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes(JWT_SCHEME_NAME,
                        new SecurityScheme()
                                .name(JWT_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }
}
