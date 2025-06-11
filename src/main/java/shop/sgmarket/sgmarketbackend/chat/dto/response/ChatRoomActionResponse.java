package shop.sgmarket.sgmarketbackend.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방-상세 액션 정보")
public record ChatRoomActionResponse(
        @Schema(description = "경매(상품) ID")  Long itemId,
        @Schema(description = "상세 페이지 URL") String detailUrl,
        @Schema(description = "거래(낙찰) 페이지 URL") String tradeUrl
) {} 