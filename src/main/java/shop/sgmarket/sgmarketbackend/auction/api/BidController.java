package shop.sgmarket.sgmarketbackend.auction.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.auction.application.BidService;
import shop.sgmarket.sgmarketbackend.auction.dto.request.BidRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.BidInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions/{auctionId}/bids")
public class BidController implements BidDocs {
    private final BidService bidService;

    @PostMapping
    public ApiResponseTemplate<BidInfoResponse> bid(
            @PathVariable Long auctionId,
            @RequestBody @Valid BidRegisterRequest bidRegisterRequest
    ) {
        BidInfoResponse response = bidService.bid(auctionId, bidRegisterRequest);

        return ApiResponseTemplate.created("입찰이 완료되었습니다", response);
    }

    @GetMapping
    public ApiResponseTemplate<SliceResponse<BidInfoResponse>> getBidsForAuction(
            @PathVariable Long auctionId,
            @ParameterObject Pageable pageable
    ) {
        SliceResponse<BidInfoResponse> response = bidService.getBidsForAuction(auctionId, pageable);

        return ApiResponseTemplate.ok("입찰 목록 조회에 성공했습니다", response);
    }

    @PostMapping("/settle")
    public ApiResponseTemplate<BidInfoResponse> settleBid(@PathVariable Long auctionId) {
        BidInfoResponse response = bidService.settleBid(auctionId);

        return ApiResponseTemplate.ok("낙찰 처리가 완료되었습니다", response);
    }
}
