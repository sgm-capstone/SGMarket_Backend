package shop.sgmarket.sgmarketbackend.auction.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;

@Getter
@AllArgsConstructor
public enum AuctionCategory {

    DIGITAL_DEVICES("디지털기기"),
    HOME_APPLIANCES("생활가전"),
    FURNITURE_INTERIOR("가구/인테리어"),
    HOME_KITCHEN("생활/주방"),
    KIDS("유아동"),
    KIDS_BOOKS("유아도서"),
    WOMENS_CLOTHING("여성의류"),
    WOMENS_ACCESSORIES("여성잡화"),
    MENS_FASHION_ACCESSORIES("남성패션/잡화"),
    BEAUTY_COSMETICS("뷰티/미용"),
    SPORTS_RECREATION("스포츠/레저"),
    HOBBIES("취미");

    private final String name;

    public static AuctionCategory from(String name) {
        for (AuctionCategory category : AuctionCategory.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.AUCTION_CATEGORY_NOT_FOUND);
    }

    public static AuctionCategory fromKebabCase(String kebabCase) {
        if (kebabCase == null || kebabCase.isEmpty()) {
            return null;
        }
        String enumName = kebabCase.toUpperCase().replace("-", "_");
        try {
            return AuctionCategory.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.AUCTION_CATEGORY_NOT_FOUND);
        }
    }
}
