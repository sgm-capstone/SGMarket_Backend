package shop.sgmarket.sgmarketbackend.auction.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AuctionRegisterRequest(
        @NotNull
        @Schema(description = "아이템 등록 요청")
        ItemRegisterRequest itemRegisterRequest,

        @NotNull
        @Schema(example = "아이폰 15 프로", description = "경매 제목")
        String title,

        @NotNull
        @Schema(example = "애플의 최신 스마트폰 아이폰 15 프로입니다. 최상의 성능과 디스플레이를 자랑합니다.", description = "경매에 대한 상세 설명")
        String description,

        @NotNull
        @Schema(example = "2025-12-31 23:59:59", description = "경매 종료 시간", type = "string")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @Future(message = "경매 종료 시간은 미래 시간이어야 합니다")
        LocalDateTime endDate,

        @NotNull
        @Min(1)
        @Schema(example = "1200000", description = "경매 시작 가격")
        int startPrice,

        @Min(1)
        @Schema(example = "1300000", description = "현재 가격")
        int currentPrice,

        @Min(1)
        @Schema(example = "1500000", description = "경매 종료 가격")
        int endPrice,

        @NotNull
        @Schema(example = "디지털기기", description = "경매 카테고리")
        String auctionCategory
) {
    public record ItemRegisterRequest(
            @NotNull
            @Schema(example = "아이폰 15 프로", description = "아이템 이름")
            String itemName,

            @NotNull
            @Schema(example = "애플", description = "경매 아이템의 브랜드 또는 제조사")
            String auction
    ) {
    }
}
