package shop.sgmarket.sgmarketbackend.notification.api.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림 SSE", description = "클라이언트가 SSE 스트림을 구독하는 API입니다.")
public interface SseDocs {

    @Operation(
        summary = "SSE 구독 시작",
        description = "클라이언트가 이 엔드포인트에 연결하면, 서버는 실시간 알림을 텍스트 이벤트 스트림으로 전송합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "SSE 연결이 성공적으로 수립되었습니다. 이후 서버에서 실시간 이벤트를 전송합니다.",
                content = @Content(
                    mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                    schema = @Schema(type = "string", description = "연결된 SSE 스트림")
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자입니다."
            )
        }
    )
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter subscribe();
}
