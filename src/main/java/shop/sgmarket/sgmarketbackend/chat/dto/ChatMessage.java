package shop.sgmarket.sgmarketbackend.chat.dto;

import shop.sgmarket.sgmarketbackend.chat.domain.MessageType;
import java.time.LocalDateTime;

public record ChatMessage(
        String roomId,
        String sender,
        String senderId,
        String message,
        MessageType type,
        LocalDateTime createdAt
) {
}
