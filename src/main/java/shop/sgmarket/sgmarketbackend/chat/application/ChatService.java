package shop.sgmarket.sgmarketbackend.chat.application;

import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.dto.ChatMessage;
import java.util.List;

public interface ChatService {
    ChatRoom createRoom(String name, Long creatorId);
    List<ChatRoom> findAllRooms();
    void sendMessage(ChatMessage message, String userId);
}