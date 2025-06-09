package shop.sgmarket.sgmarketbackend.chat.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.domain.MessageType;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatMessage;
import shop.sgmarket.sgmarketbackend.chat.dto.response.DirectChatListResponse;
import shop.sgmarket.sgmarketbackend.chat.repository.ChatRoomRepository;
import shop.sgmarket.sgmarketbackend.global.config.RedisPublisher;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ChatRoom createRoom(String name, Long creatorId) {
        return chatRoomRepository.createRoom(name, creatorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAllRooms();
    }

    @Override
    public void sendMessage(ChatMessage message, String userId) {
        Member sender = memberRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        MessageType type = message.type() != null ? message.type() : MessageType.TALK;

        ChatMessage authenticatedMessage = new ChatMessage(
                message.roomId(),
                sender.getNickname(),
                userId,
                sender.getOauthInfo().getOauthProfileImageUrl(),
                message.message(),
                type,
                LocalDateTime.now()
        );
        
        redisPublisher.publish(authenticatedMessage);
        chatMessageService.saveMessage(authenticatedMessage.roomId(), authenticatedMessage);
    }

    @Override
    @Transactional
    public ChatRoom createDirectChat(Long senderId, Long receiverId, Long itemId, String initialMessage) {
        ChatRoom existingRoom = chatRoomRepository.findDirectChat(senderId, receiverId, itemId);
        if (existingRoom != null) {
            return existingRoom;
        }

        String roomId = UUID.randomUUID().toString();
        String roomName = String.format("direct_%d_%d_item_%d", Math.min(senderId, receiverId), Math.max(senderId, receiverId), itemId);

        ChatRoom room = ChatRoom.builder().id(roomId).name(roomName).creatorId(senderId).isDirectChat(true).participantId(receiverId).itemId(itemId).build();

        ChatRoom savedRoom = chatRoomRepository.createRoom(room);

        if (initialMessage != null && !initialMessage.trim().isEmpty()) {
            Member senderMember = memberRepository.findById(senderId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            
            ChatMessage message = new ChatMessage(
                    roomId,
                    senderMember.getNickname(),
                    senderId.toString(),
                    senderMember.getOauthInfo().getOauthProfileImageUrl(),
                    initialMessage,
                    MessageType.TALK,
                    LocalDateTime.now()
            );
            redisPublisher.publish(message);
            chatMessageService.saveMessage(roomId, message);
        }

        return savedRoom;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoom> findDirectChats(Long userId) {
        return chatRoomRepository.findDirectChatsByUserId(userId);
    }

    @Override
    public boolean isDirectChat(String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        return room != null && room.isDirectChat();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirectChatListResponse> findDirectChatsWithDetails(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findDirectChatsByUserId(userId);

        return chatRooms.stream().map(room -> {
            // 상대방 ID 찾기
            Long otherUserId = room.getCreatorId().equals(userId) ? room.getParticipantId() : room.getCreatorId();

            // 상대방 정보 조회
            Member otherUser = memberRepository.findById(otherUserId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            // 마지막 메시지 조회
            List<ChatMessage> messages = chatMessageService.getMessages(room.getId(), 1);
            ChatMessage lastMessage = messages.isEmpty() ? null : messages.get(0);

            String locationName = otherUser.getLocation() != null ? otherUser.getLocation().getAddress() : null;

            return new DirectChatListResponse(room.getId(), otherUserId, otherUser.getNickname(), otherUser.getOauthInfo().getOauthProfileImageUrl(),locationName, lastMessage != null ? lastMessage.message() : null, lastMessage != null ? lastMessage.createdAt() : null, room.getItemId());
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRoom(String roomId) {
        chatRoomRepository.deleteRoom(roomId);
        chatMessageService.deleteMessages(roomId);
    }
}