package shop.sgmarket.sgmarketbackend.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DM 목록 응답")
public record DirectChatListResponse(
        @Schema(description = "채팅방 ID") String roomId,
        @Schema(description = "상대방 ID") Long otherUserId,
        @Schema(description = "상대방 닉네임") String otherUserNickname,
        @Schema(description = "상대방 프로필 이미지 URL") String otherUserProfileImage,
        @Schema(description = "마지막 메시지") String lastMessage,
        @Schema(description = "상대방의 지역 이름") String locationName,
        @Schema(description = "마지막 메시지 시간") LocalDateTime lastMessageTime,
        @Schema(description = "연결된 상품 ID") Long itemId
) {}