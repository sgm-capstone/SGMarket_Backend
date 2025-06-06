package shop.sgmarket.sgmarketbackend.chat.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatMessage;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatMessagePage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

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
                .map(obj -> obj instanceof ChatMessage
                        ? (ChatMessage) obj
                        : objectMapper.convertValue(obj, ChatMessage.class))
                .toList();
    }

    // 채팅방 메시지 전체 삭제
    public void deleteMessages(String roomId) {
        redisTemplate.delete(CHAT_KEY_PREFIX + roomId);
    }

    // 메시지 불러오기 (커서 기반, 오래된 → 최신 순)
    public ChatMessagePage getMessagesPage(String roomId, Long cursor, int size) {
        String key   = CHAT_KEY_PREFIX + roomId;
        Long total   = redisTemplate.opsForList().size(key);
        if (total == null || total == 0) {
            return new ChatMessagePage(List.of(), null, false);
        }

        // cursor 가 없으면 최신부터 조회하므로 endExclusive = total
        long endExclusive = (cursor == null) ? total : cursor;
        long start        = Math.max(0, endExclusive - size);
        var rawList       = redisTemplate.opsForList().range(key, start, endExclusive - 1);

        List<ChatMessage> messages = rawList.stream()
                .map(obj -> obj instanceof ChatMessage
                        ? (ChatMessage) obj
                        : objectMapper.convertValue(obj, ChatMessage.class))
                .toList();

        Long nextCursor = (start == 0) ? null : start;
        boolean hasMore = nextCursor != null;

        return new ChatMessagePage(messages, nextCursor, hasMore);
    }
}
