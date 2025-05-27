package shop.sgmarket.sgmarketbackend.chat.dto.request;

public record DirectChatRequest(
    Long receiverId, // 채팅을 받을 사용자의 ID
    Long itemId, // 관련된 아이템의 ID (optional)
    String initialMessage  // 첫 메시지
) {}
