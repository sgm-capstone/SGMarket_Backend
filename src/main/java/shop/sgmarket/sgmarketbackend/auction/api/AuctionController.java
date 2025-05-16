package shop.sgmarket.sgmarketbackend.auction.api;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.auction.application.AuctionLikeService;
import shop.sgmarket.sgmarketbackend.auction.application.AuctionService;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionToggleLikeResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.PriceHistoryInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionController implements AuctionDocs {

    private final AuctionService auctionService;
    private final AuctionLikeService auctionLikeService;

    @Override
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseTemplate<AuctionInfoResponse> registerAuction(
            @Valid @RequestPart AuctionRegisterRequest request,
            @RequestPart MultipartFile itemImage
    ) {
        AuctionInfoResponse response = auctionService.register(request, itemImage);

        return ApiResponseTemplate.created("경매 등록에 성공했습니다.", response);
    }

    @Override
    @GetMapping("/{auctionId}")
    public ApiResponseTemplate<AuctionInfoResponse> getAuction(@PathVariable Long auctionId) {
        AuctionInfoResponse response = auctionService.getAuction(auctionId);

        return ApiResponseTemplate.ok("경매 조회에 성공했습니다.", response);
    }

    @Override
    @GetMapping("/{auctionId}/others")
    public ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getOtherAuctionsBySameMember(
            @PathVariable Long auctionId,
            @ParameterObject Pageable pageable
    ) {
        SliceResponse<AuctionInfoResponse> response = auctionService.getOtherAuctionsBySameMember(auctionId, pageable);

        return ApiResponseTemplate.ok("해당 작성자의 다른 경매 목록 조회에 성공했습니다.", response);
    }

    @Override
    @GetMapping
    public ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getAuctionsByAddressAndCategory(
            @RequestParam(value = "category", required = false) String category,
            @ParameterObject Pageable pageable
    ) {
        SliceResponse<AuctionInfoResponse> response =
                auctionService.getAuctionsByAddressAndCategory(category, pageable);

        return ApiResponseTemplate.ok("주소 및 카테고리 기반 경매 목록 조회에 성공했습니다.", response);
    }

    @Override
    @PatchMapping(
            value = "/{auctionId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiResponseTemplate<AuctionInfoResponse> updateAuction(
            @PathVariable Long auctionId,
            @Valid @RequestPart AuctionUpdateRequest request,
            @RequestPart(value = "itemImage", required = false) MultipartFile itemImage
    ) {
        AuctionInfoResponse response = auctionService.updateAuction(auctionId, request, itemImage);

        return ApiResponseTemplate.ok("경매 수정에 성공했습니다.", response);
    }

    @GetMapping("/{auctionId}/price-history")
    public ApiResponseTemplate<List<PriceHistoryInfoResponse>> getPriceHistoryByAuctionId(
            @PathVariable Long auctionId
    ) {
        List<PriceHistoryInfoResponse> response = auctionService.getPriceHistoryByAuctionId(auctionId);

        return ApiResponseTemplate.ok(response)
                .message("경매 가격 이력 조회에 성공했습니다.");
    }

    @Override
    @DeleteMapping("/{auctionId}")
    public ApiResponseTemplate<Void> deleteAuction(@PathVariable Long auctionId) {
        auctionService.deleteAuction(auctionId);

        return ApiResponseTemplate.ok("경매 삭제에 성공했습니다.", null);
    }

    @Override
    @PostMapping("/{auctionId}/like")
    public ApiResponseTemplate<AuctionToggleLikeResponse> toggleLike(
            @PathVariable Long auctionId
    ) {
        AuctionToggleLikeResponse response = auctionLikeService.toggleLike(auctionId);

        return ApiResponseTemplate.ok("경매 좋아요 토글에 성공했습니다.", response);
    }
}
