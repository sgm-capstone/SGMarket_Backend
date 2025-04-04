package shop.sgmarket.sgmarketbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SgMarketBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgMarketBackendApplication.class, args);
    }

}
