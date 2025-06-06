package shop.sgmarket.sgmarketbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import shop.sgmarket.sgmarketbackend.global.config.WebSocketConfig;

@ConfigurationPropertiesScan
@SpringBootApplication
@Import(WebSocketConfig.class)
public class SgMarketBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgMarketBackendApplication.class, args);
    }

}
