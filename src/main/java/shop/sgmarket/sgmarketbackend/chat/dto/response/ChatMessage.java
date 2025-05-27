package shop.sgmarket.sgmarketbackend.chat.dto.response;

import shop.sgmarket.sgmarketbackend.chat.domain.MessageType;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 메시지")
public record ChatMessage(
        @Schema(description = "채팅방 ID") String roomId,
        @Schema(description = "보낸 사람 닉네임") String sender,
        @Schema(description = "보낸 사람 ID") String senderId,
        @Schema(description = "메시지 내용") String message,
        @Schema(description = "메시지 타입 (ENTER, TALK, LEAVE)") MessageType type,
        @Schema(description = "생성 시각") LocalDateTime createdAt
) {
}
