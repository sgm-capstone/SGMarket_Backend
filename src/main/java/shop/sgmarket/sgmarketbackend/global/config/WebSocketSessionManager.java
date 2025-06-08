package shop.sgmarket.sgmarketbackend.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
public class WebSocketSessionManager {

    @Getter
    private static class SessionWrapper {
        private final WebSocketSession session;
        private final Instant lastActiveTime;

        public SessionWrapper(WebSocketSession session) {
            this.session = session;
            this.lastActiveTime = Instant.now();
        }

    }

    private final Map<String, SessionWrapper> sessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long INACTIVITY_LIMIT_MINUTES = 30;

    @PostConstruct
    public void init() {
        scheduler.scheduleWithFixedDelay(this::cleanupSessions, 1, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
    }

    private void cleanupSessions() {
        Instant now = Instant.now();
        sessions.entrySet().removeIf(entry -> {
            SessionWrapper wrapper = entry.getValue();
            try {
                WebSocketSession session = wrapper.getSession();
                if (!session.isOpen()) {
                    log.info("Removing closed session: {}", entry.getKey());
                    return true;
                }
                long inactiveMinutes = Duration.between(wrapper.getLastActiveTime(), now).toMinutes();
                if (inactiveMinutes >= INACTIVITY_LIMIT_MINUTES) {
                    log.info("Removing inactive session ({} minutes): {}", inactiveMinutes, entry.getKey());
                    session.close();
                    return true;
                }
                return false;
            } catch (Exception e) {
                log.warn("Error checking session {}: {}", entry.getKey(), e.getMessage());
                return true;
            }
        });
    }
}
