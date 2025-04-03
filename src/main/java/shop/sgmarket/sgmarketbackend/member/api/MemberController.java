package shop.sgmarket.sgmarketbackend.member.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.member.application.MemberService;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberUpdateResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ApiResponseTemplate<MemberUpdateResponse> updateProfile(
            @RequestBody @Valid final MemberUpdateRequest memberUpdateRequest) {

        return ApiResponseTemplate.ok(memberService.updateProfile(memberUpdateRequest))
                .message("주소 변경 완료");
    }
}
