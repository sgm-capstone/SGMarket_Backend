package shop.sgmarket.sgmarketbackend.chat.dto.response;

import java.util.List;

/**
 * 채팅방 메시지 페이지 응답
 * @param messages   이번에 가져온 메시지들(오래된 → 최신 순)
 * @param nextCursor 다음 호출 시 사용할 cursor(null 이면 더 없음)
 * @param hasMore    더 가져올 메시지가 있는지 여부
 */
public record ChatMessagePage(
        List<ChatMessage> messages,
        Long nextCursor,
        boolean hasMore
) { }