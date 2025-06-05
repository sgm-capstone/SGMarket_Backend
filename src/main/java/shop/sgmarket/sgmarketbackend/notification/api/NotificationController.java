package shop.sgmarket.sgmarketbackend.notification.api;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.notification.application.NotificationQueryService;
import shop.sgmarket.sgmarketbackend.notification.domain.Notification;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    @GetMapping
    public ApiResponseTemplate<Page<Notification>> getMyNotifications(@ParameterObject Pageable pageable) {
        Page<Notification> page = notificationQueryService.getNotificationsByMemberId(pageable);
        return ApiResponseTemplate.ok(page).message("알림 목록 조회 성공");
    }

    @PatchMapping("/{notificationId}")
    public ApiResponseTemplate<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationQueryService.markAsRead(notificationId);
        return ApiResponseTemplate.ok();
    }
}
