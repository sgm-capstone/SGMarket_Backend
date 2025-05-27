package shop.sgmarket.sgmarketbackend.chat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    private String id;
    private String name;
    private Long creatorId;
    private LocalDateTime createdAt;
    private boolean isDirectChat;
    private Long participantId;  // 1:1 채팅의 경우 상대방 ID
    private Long itemId;

    @Builder
    public ChatRoom(String id, String name, Long creatorId, boolean isDirectChat, Long participantId, Long itemId) {
        this.id = id;
        this.name = name;
        this.creatorId = creatorId;
        this.createdAt = LocalDateTime.now();
        this.isDirectChat = isDirectChat == true;  // null 체크
        this.participantId = participantId;
        this.itemId = itemId;

    }
}
