package shop.sgmarket.sgmarketbackend.global.init;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionCategoryRepository;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {

    private final AuctionCategoryRepository auctionCategoryRepository;

    @Override
    public void run(String... args) {
        List<String> categories = List.of(
                "디지털기기",
                "생활가전",
                "가구/인테리어",
                "생활/주방",
                "유아동",
                "유아도서",
                "여성의류",
                "여성잡화",
                "남성패션/잡화",
                "뷰티/미용",
                "스포츠/레저",
                "취미"
        );

        for (String name : categories) {
            auctionCategoryRepository.findByName(name)
                    .orElseGet(() -> auctionCategoryRepository.save(AuctionCategory.createItemCategory(name)));
        }
    }
}
