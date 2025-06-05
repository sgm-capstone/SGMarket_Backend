package shop.sgmarket.sgmarketbackend.notification.api;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.notification.application.NotificationQueryService;
import shop.sgmarket.sgmarketbackend.notification.dto.NotificationInfoResponse;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    @GetMapping
    public ApiResponseTemplate<SliceResponse<NotificationInfoResponse>> getMyNotifications(
            @ParameterObject Pageable pageable) {
        SliceResponse<NotificationInfoResponse> response = notificationQueryService.getNotificationsByMemberId(
                pageable);

        return ApiResponseTemplate.ok(response).message("알림 목록 조회 성공");
    }

    @PatchMapping("/{notificationId}")
    public ApiResponseTemplate<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationQueryService.markAsRead(notificationId);
        return ApiResponseTemplate.ok();
    }
}
