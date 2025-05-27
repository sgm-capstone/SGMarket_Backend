package shop.sgmarket.sgmarketbackend.chat.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CHAT_KEY_PREFIX = "chat:room:";

    // 메시지 저장
    public void saveMessage(String roomId, ChatMessage message) {
        String key = CHAT_KEY_PREFIX + roomId;
        redisTemplate.opsForList().rightPush(key, message);
    }

    // 메시지 불러오기 (최신 count개)
    public List<ChatMessage> getMessages(String roomId, int count) {
        String key = CHAT_KEY_PREFIX + roomId;
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) return List.of();
        long start = Math.max(0, size - count);
        return redisTemplate.opsForList().range(key, start, size - 1)
                .stream()
                .map(obj -> (ChatMessage) obj)
                .toList();
    }
}