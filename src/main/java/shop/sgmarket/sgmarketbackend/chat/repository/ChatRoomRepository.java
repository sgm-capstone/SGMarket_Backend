package shop.sgmarket.sgmarketbackend.chat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

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
                .isDirectChat(false)
                .build();
        em.persist(room);
        return room;
    }

    @Transactional
    public ChatRoom createRoom(ChatRoom room) {
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

    public ChatRoom findDirectChat(Long userId1, Long userId2, Long itemId) {
        String roomName = String.format("direct_%d_%d_item_%d", Math.min(userId1, userId2), Math.max(userId1, userId2),itemId);
        try {
            return em.createQuery("SELECT c FROM ChatRoom c WHERE c.name = :name AND c.isDirectChat = true", ChatRoom.class)
                    .setParameter("name", roomName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ChatRoom> findDirectChatsByUserId(Long userId) {
        return em.createQuery("SELECT c FROM ChatRoom c WHERE c.isDirectChat = true AND (c.creatorId = :userId OR c.participantId = :userId)", ChatRoom.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Transactional
    public void deleteRoom(String roomId) {
        ChatRoom room = findRoomById(roomId);
        if (room != null) {
            em.remove(room);
        }
    }
}