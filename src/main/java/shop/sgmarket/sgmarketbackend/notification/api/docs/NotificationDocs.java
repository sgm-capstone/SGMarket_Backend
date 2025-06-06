package shop.sgmarket.sgmarketbackend.notification.api.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.notification.dto.NotificationInfoResponse;

@Tag(name = "알림 API", description = "회원 알림 관련 API입니다.")
public interface NotificationDocs {

    @Operation(
        summary = "내 알림 목록 조회",
        description = "페이지 단위로 현재 로그인한 회원의 알림 목록을 조회합니다.",
        parameters = {
            @Parameter(
                name = "pageable",
                description = "페이징 정보 (page, size, sort)",
                in = ParameterIn.QUERY,
                required = false,
                schema = @Schema(implementation = Pageable.class)
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "알림 목록 조회 성공",
                content = @Content(
                    schema = @Schema(implementation = SliceResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자입니다."
            )
        }
    )
    @GetMapping
    ApiResponseTemplate<SliceResponse<NotificationInfoResponse>> getMyNotifications(
        @ParameterObject Pageable pageable
    );

    @Operation(
        summary = "알림 읽음 처리",
        description = "알림 ID를 지정하여 해당 알림을 읽음 상태로 변경합니다.",
        parameters = {
            @Parameter(
                name = "notificationId",
                description = "읽음 처리할 알림의 ID",
                required = true,
                in = ParameterIn.PATH,
                schema = @Schema(type = "long", example = "1")
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "알림 읽음 처리 성공"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "해당 알림을 찾을 수 없습니다."
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자입니다."
            )
        }
    )
    @PatchMapping("/{notificationId}")
    ApiResponseTemplate<Void> markNotificationAsRead(
        @Parameter(name = "notificationId", hidden = true) Long notificationId
    );
}
