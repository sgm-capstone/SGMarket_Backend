package shop.sgmarket.sgmarketbackend.auction.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.PageResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@Tag(name = "Auction", description = "경매 API")
public interface AuctionDocs {

    @Operation(summary = "경매 등록", description = "새로운 경매를 등록합니다.")
    ApiResponseTemplate<AuctionInfoResponse> registerAuction(
            @Valid @RequestBody AuctionRegisterRequest request
    );

    @Operation(summary = "경매 조회", description = "경매 ID로 경매를 조회합니다.")
    ApiResponseTemplate<AuctionInfoResponse> getAuction(
            @Parameter(description = "조회할 경매 ID", required = true)
            @PathVariable Long auctionId
    );

    @Operation(summary = "경매 목록 조회", description = "모든 경매를 페이지네이션하여 조회합니다.")
    ApiResponseTemplate<PageResponse<AuctionInfoResponse>> getAllAuctions(
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "경매 수정", description = "경매 ID로 기존 경매를 수정합니다.")
    ApiResponseTemplate<AuctionInfoResponse> updateAuction(
            @Parameter(description = "수정할 경매 ID", required = true)
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionUpdateRequest request
    );

    @Operation(summary = "경매 삭제", description = "경매 ID로 경매를 삭제합니다.")
    ApiResponseTemplate<Void> deleteAuction(
            @Parameter(description = "삭제할 경매 ID", required = true)
            @PathVariable Long auctionId
    );
}
