package shop.sgmarket.sgmarketbackend.auction.api;

import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.auction.application.AuctionService;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.PageResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionController implements AuctionDocs {

    private final AuctionService auctionService;

    @Override
    @PatchMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseTemplate<AuctionInfoResponse> registerAuction(
            @Valid @RequestPart("request") AuctionRegisterRequest request,
            @RequestPart(value = "itemImage", required = false) MultipartFile itemImage
    ) throws IOException {
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
    @GetMapping
    public ApiResponseTemplate<PageResponse<AuctionInfoResponse>> getAllAuctions(@ParameterObject Pageable pageable) {
        return ApiResponseTemplate.ok("경매 목록 조회에 성공했습니다.", auctionService.getAllAuctions(pageable));
    }

    @Override
    @PutMapping("/{auctionId}")
    public ApiResponseTemplate<AuctionInfoResponse> updateAuction(
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionUpdateRequest request) {
        AuctionInfoResponse response = auctionService.updateAuction(auctionId, request);
        return ApiResponseTemplate.ok("경매 수정에 성공했습니다.", response);
    }

    @Override
    @DeleteMapping("/{auctionId}")
    public ApiResponseTemplate<Void> deleteAuction(@PathVariable Long auctionId) {
        auctionService.deleteAuction(auctionId);
        return ApiResponseTemplate.ok("경매 삭제에 성공했습니다.", null);
    }
}
