package shop.sgmarket.sgmarketbackend.chat.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import shop.sgmarket.sgmarketbackend.chat.application.ChatMessageService;
import shop.sgmarket.sgmarketbackend.chat.application.ChatService;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.dto.ChatMessage;
import shop.sgmarket.sgmarketbackend.chat.dto.DirectChatRequest;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;

import java.security.Principal;
import java.util.List;

@Tag(
        name        = "채팅 API",
        description = "채팅방/DM 생성·조회 등 채팅 관련 REST API"
)
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemberUtil  memberUtil;
    private final ChatMessageService chatMessageService;

    // --------------------------- 그룹 채팅 --------------------------- //

    @Operation(
            summary     = "그룹 채팅방 생성",
            description = "요청 본문으로 전달된 방 이름을 사용해 새로운 그룹 채팅방을 생성합니다. " +
                    "생성자는 현재 로그인한 사용자입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChatRoom.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @PostMapping("/room")
    public ChatRoom createRoom(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "채팅방 생성 요청",
                    required    = true,
                    content     = @Content(schema = @Schema(implementation = ChatRoomRequest.class))
            )
            @RequestBody ChatRoomRequest request
    ) {
        Long memberId = memberUtil.getCurrentMember().getId();
        return chatService.createRoom(request.name(), memberId);
    }

    @Operation(
            summary     = "모든 그룹 채팅방 목록 조회",
            description = "서버에 존재하는 모든 그룹 채팅방을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ChatRoom.class)))
    @GetMapping("/rooms")
    public List<ChatRoom> getRooms() {
        return chatService.findAllRooms();
    }

    // --------------------------- 1:1 DM --------------------------- //

    @Operation(
            summary     = "1:1 DM 채팅방 생성",
            description = "요청 본문에 받는 사람 ID와 첫 메시지를 포함하여 1:1 대화방을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "DM 방 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChatRoom.class))),
            @ApiResponse(responseCode = "404", description = "수신자 회원을 찾을 수 없음")
    })
    @PostMapping("/direct")
    public ChatRoom createDirectChat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DM 생성 요청",
                    required    = true,
                    content     = @Content(schema = @Schema(implementation = DirectChatRequest.class))
            )
            @RequestBody DirectChatRequest request
    ) {
        Long senderId = memberUtil.getCurrentMember().getId();
        return chatService.createDirectChat(senderId, request.receiverId(), request.initialMessage());
    }

    @Operation(
            summary     = "내 DM 목록 조회",
            description = "현재 로그인한 사용자가 참여 중인 모든 1:1 DM 채팅방을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ChatRoom.class)))
    @GetMapping("/direct")
    public List<ChatRoom> getDirectChats() {
        Long userId = memberUtil.getCurrentMember().getId();
        return chatService.findDirectChats(userId);
    }

    // --------------------------- WebSocket --------------------------- //
    // Swagger 문서에 노출할 필요가 없으므로 Hidden 처리
    @Hidden
    @MessageMapping("/chat/message")
    @SendTo("/sub/chat/room")
    public void message(
            @Parameter(description = "채팅 메시지") @Payload ChatMessage message,
            Principal principal
    ) {
        String userId = principal.getName();
        chatService.sendMessage(message, userId);
    }

    // 채팅방 메시지 내역 조회
    @Operation(
        summary = "채팅방 메시지 내역 조회",
        description = "특정 채팅방의 최신 메시지 내역을 조회합니다.",
        parameters = {
            @Parameter(name = "roomId", description = "채팅방 ID", required = true),
            @Parameter(name = "count", description = "가져올 메시지 개수(기본값 50)", required = false)
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "메시지 내역 조회 성공",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessage.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증 실패"
            )
        }
    )
    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessage> getChatMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int count) {
        return chatMessageService.getMessages(roomId, count);
    }
}
