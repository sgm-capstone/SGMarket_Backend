package shop.sgmarket.sgmarketbackend.member.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sgmarket.sgmarketbackend.global.response.ApiResponseTemplate;
import shop.sgmarket.sgmarketbackend.member.application.MemberService;
import shop.sgmarket.sgmarketbackend.member.dto.request.UpdateAddressRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.UpdateAddressResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/address")
    public ApiResponseTemplate<UpdateAddressResponse> updateAddress(UpdateAddressRequest updateAddressRequest) {
        return ApiResponseTemplate.ok(memberService.updateAddress(updateAddressRequest))
                .message("주소 변경 완료");
    }
}
