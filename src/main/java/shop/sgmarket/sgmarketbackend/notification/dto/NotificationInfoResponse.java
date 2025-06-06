package shop.sgmarket.sgmarketbackend.notification.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import shop.sgmarket.sgmarketbackend.notification.domain.Notification;
import shop.sgmarket.sgmarketbackend.notification.domain.NotificationEventType;

@Builder
public record NotificationInfoResponse(
        Long notificationId,
        NotificationEventType eventType,
        String message,
        LocalDateTime occurredAt,
        boolean isRead
) {
    public static NotificationInfoResponse from(Notification notification) {
        return NotificationInfoResponse.builder()
                .notificationId(notification.getId())
                .eventType(notification.getEventType())
                .message(notification.getMessage())
                .occurredAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .build();
    }
}
