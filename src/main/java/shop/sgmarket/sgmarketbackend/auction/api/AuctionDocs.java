package shop.sgmarket.sgmarketbackend.auction.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionToggleLikeResponse;
import shop.sgmarket.sgmarketbackend.auction.dto.response.PriceHistoryInfoResponse;
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

    @Operation(summary = "해당 작성자의 다른 경매 목록 조회", description = "특정 경매 작성자의 다른 활성 경매 목록을 조회합니다.")
    ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getOtherAuctionsBySameMember(
            @Parameter(description = "조회할 경매 ID", required = true)
            @PathVariable Long auctionId,

            @Parameter(description = "페이지네이션 정보", required = true)
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );


    @Operation(summary = "주소 및 카테고리 기반 경매 목록 조회", description = "주소와 카테고리 기준으로 경매 목록을 조회합니다.")
    ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getAuctionsByAddressAndCategory(
            @Parameter(
                    description = "카테고리 예시: digital-devices, home-appliances, furniture-interior, home-kitchen, kids,"
                            + " kids-books, womens-clothing, womens-accessories, mens-fashion-accessories, beauty-cosmetics,"
                            + " sports-recreation, hobbies"
            )
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

    @Operation(
            summary = "경매 시세 이력 조회",
            description = "경매 ID로 해당 경매의 입찰 가격 이력을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "경매 가격 이력 조회에 성공했습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 경매를 찾을 수 없습니다.")
            }
    )
    @GetMapping("/auctions/{auctionId}/price-history")
    ApiResponseTemplate<List<PriceHistoryInfoResponse>> getPriceHistoryByAuctionId(
            @Parameter(description = "시세 이력을 조회할 경매 ID", required = true)
            @PathVariable Long auctionId
    );

    @Operation(summary = "경매 삭제", description = "경매 ID로 경매를 삭제합니다.")
    ApiResponseTemplate<Void> deleteAuction(
            @Parameter(description = "삭제할 경매 ID", required = true)
            @PathVariable Long auctionId
    );

    @Operation(
            summary = "경매 좋아요 토글",
            description = "특정 경매에 대해 좋아요를 추가하거나 제거합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "경매 좋아요 토글에 성공했습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 경매가 존재하지 않습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
            }
    )
    @PostMapping("/auctions/{auctionId}/like")
    ApiResponseTemplate<AuctionToggleLikeResponse> toggleLike(
            @Parameter(description = "좋아요를 토글할 경매 ID")
            @PathVariable Long auctionId
    );
}
