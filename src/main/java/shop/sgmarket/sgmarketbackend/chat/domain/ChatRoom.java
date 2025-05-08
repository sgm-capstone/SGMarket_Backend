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

    @Builder
    public ChatRoom(String id, String name, Long creatorId) {
        this.id = id;
        this.name = name;
        this.creatorId = creatorId;
        this.createdAt = LocalDateTime.now();
    }
}
