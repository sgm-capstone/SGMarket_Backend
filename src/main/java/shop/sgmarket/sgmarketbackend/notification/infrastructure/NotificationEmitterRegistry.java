package shop.sgmarket.sgmarketbackend.notification.infrastructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import shop.sgmarket.sgmarketbackend.notification.dto.NotificationInfoResponse;

@Slf4j
@Component
public class NotificationEmitterRegistry implements DisposableBean {

    private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000L;   // 30분
    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 500L;               // 재시도 딜레이
    private static final long HEARTBEAT_INTERVAL_MS = 15_000L;     // 15초마다 heartbeat 전송

    private final Map<Long, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();
    private final Map<SseEmitter, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        addEmitter(memberId, emitter);
        registerLifecycleCallbacks(memberId, emitter);
        scheduleHeartbeat(memberId, emitter);

        return emitter;
    }

    private void addEmitter(Long memberId, SseEmitter emitter) {
        emitterMap.compute(memberId, (key, emitterList) -> {
            if (emitterList == null) {
                emitterList = new ArrayList<>();
            }
            emitterList.add(emitter);
            return emitterList;
        });
        log.debug("[EmitterRegistry] 새로운 Emitter 등록: memberId={}, 현재 구독 개수={}",
                memberId, emitterMap.get(memberId).size());
    }

    private void registerLifecycleCallbacks(Long memberId, SseEmitter emitter) {
        emitter.onCompletion(() -> removeEmitter(memberId, emitter));

        emitter.onTimeout(() -> {
            log.debug("[EmitterRegistry] Timeout 발생: memberId={}, emitter={}", memberId, emitter);
            emitter.complete();
            removeEmitter(memberId, emitter);
        });

        emitter.onError((ex) -> {
            log.warn("[EmitterRegistry] 전송 오류 발생: memberId={}, message={}", memberId, ex.getMessage());
            emitter.completeWithError(ex);
            removeEmitter(memberId, emitter);
        });
    }

    private void scheduleHeartbeat(Long memberId, SseEmitter emitter) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (IOException e) {
                log.warn("[EmitterRegistry] Heartbeat 전송 실패: memberId={}, emitter={}, message={}",
                        memberId, emitter, e.getMessage());
                removeEmitter(memberId, emitter);
            }
        }, HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);

        heartbeatTasks.put(emitter, future);
    }

    public void sendEvent(Long memberId, NotificationInfoResponse payload) {
        List<SseEmitter> emitterList = emitterMap.get(memberId);
        if (emitterList == null || emitterList.isEmpty()) {
            log.debug("[EmitterRegistry] 전송 실패(구독 없음): memberId={}", memberId);
            return;
        }

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(String.valueOf(payload.notificationId()))
                .name(payload.eventType().toString())
                .data(payload);

        for (SseEmitter emitter : new ArrayList<>(emitterList)) {
            boolean sent = false;

            for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
                try {
                    emitter.send(event);
                    sent = true;
                    log.debug("[EmitterRegistry] 이벤트 전송 성공: memberId={}, eventType={}, attempt={}",
                            memberId, payload.eventType(), attempt);
                    break;
                } catch (IOException e) {
                    log.warn("[EmitterRegistry] 이벤트 전송 실패: memberId={}, eventType={}, attempt={}, message={}",
                            memberId, payload.eventType(), attempt, e.getMessage());
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            if (!sent) {
                log.warn("[EmitterRegistry] Emitter 제거(재시도 실패): memberId={}, emitter={}", memberId, emitter);
                removeEmitter(memberId, emitter);
            }
        }
    }

    private void removeEmitter(Long memberId, SseEmitter emitter) {
        List<SseEmitter> emitterList = emitterMap.get(memberId);
        if (emitterList != null) {
            boolean removed = emitterList.remove(emitter);
            if (removed) {
                log.debug("[EmitterRegistry] Emitter 제거 완료: memberId={}, 남은 구독 개수={}",
                        memberId, emitterList.size());
            }
            if (emitterList.isEmpty()) {
                emitterMap.remove(memberId);
                log.debug("[EmitterRegistry] 모든 Emitter 제거: memberId={} (Map에서 key 삭제)", memberId);
            }
        }

        ScheduledFuture<?> future = heartbeatTasks.remove(emitter);
        if (future != null) {
            future.cancel(true);
            log.debug("[EmitterRegistry] Heartbeat 스케줄 취소: memberId={}, emitter={}", memberId, emitter);
        }
    }

    @Override
    public void destroy() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("[EmitterRegistry] scheduler 종료 완료");
    }
}
