package shop.sgmarket.sgmarketbackend.chat.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.repository.auction.AuctionRepository;
import shop.sgmarket.sgmarketbackend.chat.domain.ChatRoom;
import shop.sgmarket.sgmarketbackend.chat.dto.response.ChatRoomMetaResponse;
import shop.sgmarket.sgmarketbackend.chat.repository.ChatRoomRepository;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class ChatRoomMetaService {

    private final ChatRoomRepository  chatRoomRepository;
    private final AuctionRepository   auctionRepository;
    private final MemberRepository    memberRepository;

    @Transactional(readOnly = true)
    public ChatRoomMetaResponse getMeta(String roomId, Long viewerId) {

        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room == null) throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);

        // ─ 상품 정보 ─
        Auction auction = auctionRepository.findById(room.getItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        // ─ 상대방 정보 ─
        Long otherId = room.getCreatorId().equals(viewerId)
                ? room.getParticipantId() : room.getCreatorId();

        Member other = memberRepository.findById(otherId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return new ChatRoomMetaResponse(
                auction.getItem().getId(),
                auction.getTitle(),
                auction.getCurrentPrice(),
                other.getNickname(),
                other.getOauthInfo().getOauthProfileImageUrl()
        );
    }
} 