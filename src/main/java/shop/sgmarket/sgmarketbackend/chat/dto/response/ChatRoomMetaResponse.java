package shop.sgmarket.sgmarketbackend.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 메타 정보")
public record ChatRoomMetaResponse(
        @Schema(description = "상품(경매) ID")        Long itemId,
        @Schema(description = "상품 제목")           String itemTitle,
        @Schema(description = "현재 가격")           Long itemPrice,
        @Schema(description = "상대방 닉네임")       String otherUserNickname,
        @Schema(description = "상대방 프로필 이미지") String otherUserProfileImage
) {} 