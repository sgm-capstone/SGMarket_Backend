package shop.sgmarket.sgmarketbackend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String published = new String(message.getBody());
            ChatMessage chatMessage = objectMapper.readValue(published, ChatMessage.class);
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.roomId(), chatMessage);
        } catch (Exception e) {
            log.error("RedisSubscriber Error", e);
        }
    }
}
