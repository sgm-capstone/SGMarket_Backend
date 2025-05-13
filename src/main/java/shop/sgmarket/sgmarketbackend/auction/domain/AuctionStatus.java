package shop.sgmarket.sgmarketbackend.auction.domain;

public enum AuctionStatus {
    BIDDING("진행중"),
    COMPLETED("완료"),
    FAILED("유찰"),
    DELETED("삭제");

    AuctionStatus(String description) {
    }
}

