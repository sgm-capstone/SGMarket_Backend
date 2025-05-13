package shop.sgmarket.sgmarketbackend.global.config;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import shop.sgmarket.sgmarketbackend.global.properties.RedisProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {

    private final RedisProperties redisProperties;
    private final RedisSubscriber redisSubscriber;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(redisProperties.host(), redisProperties.port());
        config.setPassword(redisProperties.password());

        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(1))
                        .shutdownTimeout(Duration.ZERO)
                        .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        
        // ObjectMapper 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 모듈 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // ISO-8601 형식으로 직렬화
        
        // RedisTemplate 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, new PatternTopic("chatroom.*"));
        return container;
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("chatroom");
    }
}
