package shop.sgmarket.sgmarketbackend.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import shop.sgmarket.sgmarketbackend.chat.dto.ChatMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public void publish(ChatMessage message) {
        String topic = "chatroom." + message.roomId();
        log.info("[RedisPublisher] Publish message to Redis: topic={}, message={}", topic, message);
        redisTemplate.convertAndSend(topic, message);
    }
}
