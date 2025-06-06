package shop.sgmarket.sgmarketbackend.notification.domain;

public enum NotificationEventType {
    INIT, // 구독 연결 시 헬스 체크용
    BID,          // 입찰 시
    BID_OUTBID,     // 최고 입찰가 아웃비드 시
    AUCTION_SETTLED,  // 경매 낙찰 시
    AUCTION_FAILED,   // 경매 낙찰 실패 시
    AUCTION_NO_BID, // 입찰자 없음 경매 마감 시
    AUCTION_END_SOON // 경매 마감 임박 알림(예: 10분 전)
}
