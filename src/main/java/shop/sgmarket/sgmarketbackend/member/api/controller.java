package shop.sgmarket.sgmarketbackend.member.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@RestController
public class controller {

    @GetMapping("/ping")
    public ApiResponseTemplate<String> ping() {
        return ApiResponseTemplate.ok("pong");
    }
}
