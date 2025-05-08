package shop.sgmarket.sgmarketbackend.chat.api;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import shop.sgmarket.sgmarketbackend.chat.application.ChatService;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.dto.ChatMessage;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemberUtil memberUtil;

    @PostMapping("/room")
    public ChatRoom createRoom(@RequestBody ChatRoomRequest request) {
        Long memberId = memberUtil.getCurrentMember().getId();
        return chatService.createRoom(request.name(), memberId);
    }

    @MessageMapping("/chat/message")
    @SendTo("/sub/chat/room")
    public void message(@Payload ChatMessage message, Principal principal) {
        String userId = principal.getName();
        chatService.sendMessage(message, userId);
    }

    @GetMapping("/rooms")
    public List<ChatRoom> getRooms() {
        return chatService.findAllRooms();
    }
}

