package shop.sgmarket.sgmarketbackend.chat.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final EntityManager em;

    @Transactional
    public ChatRoom createRoom(String name, Long creatorId) {
        String id = UUID.randomUUID().toString();
        ChatRoom room = ChatRoom.builder()
                .id(id)
                .name(name)
                .creatorId(creatorId)
                .build();
        em.persist(room);
        return room;
    }

    public ChatRoom findRoomById(String roomId) {
        return em.find(ChatRoom.class, roomId);
    }

    public List<ChatRoom> findAllRooms() {
        return em.createQuery("select c from ChatRoom c", ChatRoom.class)
                .getResultList();
    }
}
