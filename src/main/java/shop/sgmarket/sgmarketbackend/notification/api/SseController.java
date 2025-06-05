package shop.sgmarket.sgmarketbackend.notification.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import shop.sgmarket.sgmarketbackend.notification.application.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return notificationService.initializeSseConnection();
    }
}
