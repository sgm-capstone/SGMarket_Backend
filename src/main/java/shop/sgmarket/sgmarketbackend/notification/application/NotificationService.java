package shop.sgmarket.sgmarketbackend.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.notification.domain.Notification;
import shop.sgmarket.sgmarketbackend.notification.domain.NotificationEventType;
import shop.sgmarket.sgmarketbackend.notification.dto.NotificationInfoResponse;
import shop.sgmarket.sgmarketbackend.notification.infrastructure.NotificationEmitterRegistry;
import shop.sgmarket.sgmarketbackend.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberUtil memberUtil;

    private final NotificationRepository notificationRepository;
    private final NotificationEmitterRegistry emitterRegistry;

    @Transactional
    public SseEmitter InitializeSseConnection() {
        Member member = memberUtil.getCurrentMember();
        SseEmitter emitter = emitterRegistry.createEmitter(member.getId());

        NotificationInfoResponse initPayload = new NotificationInfoResponse(
                null,
                NotificationEventType.INIT,
                "SSE 연결이 성공적으로 열렸습니다.",
                null
        );
        emitterRegistry.sendEvent(member.getId(), initPayload);

        return emitter;
    }

    @Transactional
    public void createAndSendNotification(Member member,
                                          NotificationEventType eventType,
                                          String message) {

        Notification notification = Notification.builder()
                .member(member)
                .eventType(eventType)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        NotificationInfoResponse payload = NotificationInfoResponse.from(notification);

        emitterRegistry.sendEvent(member.getId(), payload);
    }
}
