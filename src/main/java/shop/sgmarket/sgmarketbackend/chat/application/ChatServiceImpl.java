package shop.sgmarket.sgmarketbackend.chat.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.dto.ChatMessage;
import shop.sgmarket.sgmarketbackend.chat.repository.ChatRoomRepository;
import shop.sgmarket.sgmarketbackend.global.config.RedisPublisher;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;

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
    }
}