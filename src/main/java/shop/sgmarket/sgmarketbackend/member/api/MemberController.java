package shop.sgmarket.sgmarketbackend.member.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.member.application.MemberService;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberRegisterRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberRegisterResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ApiResponseTemplate<MemberRegisterResponse> updateAddress(MemberRegisterRequest memberRegisterRequest) {
        return ApiResponseTemplate.ok(memberService.updateAddress(memberRegisterRequest))
                .message("주소 변경 완료");
    }
}
