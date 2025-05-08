package shop.sgmarket.sgmarketbackend.auction.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;

@Tag(name = "경매 API", description = "경매 관련 API입니다.")
public interface AuctionDocs {

    @Operation(summary = "경매 등록", description = "새로운 경매를 등록합니다.")
    ApiResponseTemplate<AuctionInfoResponse> registerAuction(
            @Parameter(
                    description = "경매 등록 정보 JSON",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuctionRegisterRequest.class))
            )
            @RequestPart("request") AuctionRegisterRequest request,

            @Parameter(description = "상품 이미지", required = true)
            @RequestPart("representImage") MultipartFile itemImage
    );

    @Operation(summary = "경매 조회", description = "경매 ID로 경매를 조회합니다.")
    ApiResponseTemplate<AuctionInfoResponse> getAuction(
            @Parameter(description = "조회할 경매 ID", required = true)
            @PathVariable Long auctionId
    );

    @Operation(summary = "주소 및 카테고리 기반 경매 목록 조회", description = "카테고리(주소) 기준으로 경매 목록을 조회합니다.")
    ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getAuctionsByAddressAndCategory(
            @Parameter(description = "카테고리")
            @RequestParam(value = "category", required = false) String category,

            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "경매 수정", description = "경매 ID로 기존 경매를 수정합니다.")
    @PatchMapping(
            value = "/{auctionId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ApiResponseTemplate<AuctionInfoResponse> updateAuction(
            @Parameter(description = "수정할 경매 ID", required = true)
            @PathVariable Long auctionId,

            @Parameter(description = "경매 수정 요청 데이터(JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("request") @Valid AuctionUpdateRequest request,

            @Parameter(description = "수정할 상품 이미지", required = false)
            @RequestPart(value = "itemImage", required = false) MultipartFile itemImage
    );

    @Operation(summary = "경매 삭제", description = "경매 ID로 경매를 삭제합니다.")
    ApiResponseTemplate<Void> deleteAuction(
            @Parameter(description = "삭제할 경매 ID", required = true)
            @PathVariable Long auctionId
    );
}
