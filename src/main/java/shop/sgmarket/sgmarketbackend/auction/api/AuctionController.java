package shop.sgmarket.sgmarketbackend.auction.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.auction.application.AuctionService;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionsResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ApiResponseTemplate<AuctionInfoResponse> registerAuction(@Valid @RequestBody AuctionRegisterRequest request) {
        AuctionInfoResponse response = auctionService.register(request);
        return ApiResponseTemplate.created("경매 등록에 성공했습니다.", response);
    }

    @GetMapping("/{auctionId}")
    public ApiResponseTemplate<AuctionInfoResponse> getAuction(@PathVariable Long auctionId) {
        AuctionInfoResponse response = auctionService.getAuction(auctionId);
        return ApiResponseTemplate.ok("경매 조회에 성공했습니다.", response);
    }

    @GetMapping
    public ApiResponseTemplate<AuctionsResponse> getAllAuctions() {
        AuctionsResponse response = auctionService.getAllAuctions();
        return ApiResponseTemplate.ok("경매 목록 조회에 성공했습니다.", response);
    }


    @PutMapping("/{auctionId}")
    public ApiResponseTemplate<AuctionInfoResponse> updateAuction(
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionUpdateRequest request
    ) {
        AuctionInfoResponse response = auctionService.updateAuction(auctionId, request);
        return ApiResponseTemplate.ok("경매 수정에 성공했습니다.", response);
    }

    @DeleteMapping("/{auctionId}")
    public ApiResponseTemplate<Void> deleteAuction(@PathVariable Long auctionId) {
        auctionService.deleteAuction(auctionId);
        return ApiResponseTemplate.ok("경매 삭제에 성공했습니다.", null);
    }
}
