package shop.sgmarket.sgmarketbackend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Object raw = redisTemplate.getValueSerializer().deserialize(message.getBody());

            ChatMessage chatMessage =
                    (raw instanceof ChatMessage)
                            ? (ChatMessage) raw
                            : objectMapper.convertValue(raw, ChatMessage.class);

            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + chatMessage.roomId(),
                    chatMessage
            );
        } catch (Exception e) {
            log.error("RedisSubscriber Error", e);
        }
    }
}
