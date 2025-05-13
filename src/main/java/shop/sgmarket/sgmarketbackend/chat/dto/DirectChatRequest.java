package shop.sgmarket.sgmarketbackend.chat.dto;

public record DirectChatRequest(
    Long receiverId,  // 채팅을 받을 사용자의 ID
    String initialMessage  // 첫 메시지
) {}
