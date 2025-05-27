package shop.sgmarket.sgmarketbackend.chat.application;

import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatMessage;
import shop.sgmarket.sgmarketbackend.chat.dto.response.DirectChatListResponse;

import java.util.List;

public interface ChatService {
    ChatRoom createRoom(String name, Long creatorId);
    List<ChatRoom> findAllRooms();
    void sendMessage(ChatMessage message, String userId);
    
    // 1:1 채팅 관련 메서드 추가
    ChatRoom createDirectChat(Long senderId, Long receiverId, Long itemId, String initialMessage);
    List<ChatRoom> findDirectChats(Long userId);
    boolean isDirectChat(String roomId);
    
    // DM 목록 조회 메서드 추가
    List<DirectChatListResponse> findDirectChatsWithDetails(Long userId);
}