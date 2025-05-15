package shop.sgmarket.sgmarketbackend.member.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.member.application.MemberService;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberInfoResponse;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberUpdateResponse;

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
    public ApiResponseTemplate<MemberUpdateResponse> updateProfile(
            @RequestBody @Valid final MemberUpdateRequest memberUpdateRequest) {

        return ApiResponseTemplate.ok(memberService.updateProfile(memberUpdateRequest))
                .message("프로필 업데이트 완료");
    }
}
