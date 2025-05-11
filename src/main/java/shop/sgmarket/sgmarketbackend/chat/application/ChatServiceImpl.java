package shop.sgmarket.sgmarketbackend.chat.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.domain.MessageType;
import shop.sgmarket.sgmarketbackend.chat.dto.ChatMessage;
import shop.sgmarket.sgmarketbackend.chat.repository.ChatRoomRepository;
import shop.sgmarket.sgmarketbackend.global.config.RedisPublisher;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final ChatMessageService chatMessageService;

    @Override
    @Transactional
    public ChatRoom createRoom(String name, Long creatorId) {
        return chatRoomRepository.createRoom(name, creatorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAllRooms();
    }

    @Override
    public void sendMessage(ChatMessage message, String userId) {
        ChatMessage authenticatedMessage = new ChatMessage(
                message.roomId(),
                message.sender(),
                userId,
                message.message(),
                message.type(),
                LocalDateTime.now()
        );
        redisPublisher.publish(authenticatedMessage);
        chatMessageService.saveMessage(message.roomId(), authenticatedMessage);
    }

    @Override
    @Transactional
    public ChatRoom createDirectChat(Long senderId, Long receiverId, String initialMessage) {
        // 이미 존재하는 1:1 채팅방이 있는지 확인
        ChatRoom existingRoom = chatRoomRepository.findDirectChat(senderId, receiverId);
        if (existingRoom != null) {
            return existingRoom;
        }

        // 새 1:1 채팅방 생성
        String roomId = UUID.randomUUID().toString();
        String roomName = String.format("direct_%d_%d", Math.min(senderId, receiverId), Math.max(senderId, receiverId));
        
        ChatRoom room = ChatRoom.builder()
                .id(roomId)
                .name(roomName)
                .creatorId(senderId)
                .isDirectChat(true)
                .participantId(receiverId)
                .build();
        
        ChatRoom savedRoom = chatRoomRepository.createRoom(room);

        // 초기 메시지가 있다면 전송
        if (initialMessage != null && !initialMessage.trim().isEmpty()) {
            ChatMessage message = new ChatMessage(
                roomId,
                "System",
                senderId.toString(),
                initialMessage,
                MessageType.TALK,
                LocalDateTime.now()
            );
            redisPublisher.publish(message);
        }

        return savedRoom;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoom> findDirectChats(Long userId) {
        return chatRoomRepository.findDirectChatsByUserId(userId);
    }

    @Override
    public boolean isDirectChat(String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        return room != null && room.isDirectChat();
    }
}