package shop.sgmarket.sgmarketbackend.member.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.member.application.MemberService;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberInfoResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController implements MemberDocs {
    private final MemberService memberService;

    @Override
    @GetMapping
    public ApiResponseTemplate<MemberInfoResponse> getMemberInfo() {
        MemberInfoResponse response = memberService.getMemberInfoFromId();

        return ApiResponseTemplate.ok(response)
                .message("회원 정보 조회 완료");
    }

    @Override
    @PatchMapping
    public ApiResponseTemplate<MemberInfoResponse> updateProfile(
            @RequestBody @Valid final MemberUpdateRequest memberUpdateRequest) {

        return ApiResponseTemplate.ok(memberService.updateProfile(memberUpdateRequest))
                .message("프로필 업데이트 완료");
    }

    @GetMapping("/auctions")
    public ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getMyAuctions(@ParameterObject Pageable pageable) {
        SliceResponse<AuctionInfoResponse> response = memberService.getMyAuctions(pageable);

        return ApiResponseTemplate.ok(response)
                .message("내 경매 목록 조회 완료");
    }

    @GetMapping("/auctions-likes")
    public ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getMyLikedAuctions(@ParameterObject Pageable pageable) {
        SliceResponse<AuctionInfoResponse> response = memberService.getMyLikedAuctions(pageable);

        return ApiResponseTemplate.ok(response)
                .message("내가 좋아요한 경매 목록 조회 완료");
    }

    @GetMapping("/auctions-bids")
    public ApiResponseTemplate<SliceResponse<AuctionInfoResponse>> getMyBiddedAuctions(@ParameterObject Pageable pageable) {
        SliceResponse<AuctionInfoResponse> response = memberService.getMyBiddedAuctions(pageable);

        return ApiResponseTemplate.ok(response)
                .message("내가 입찰한 경매 목록 조회 완료");
    }
}
