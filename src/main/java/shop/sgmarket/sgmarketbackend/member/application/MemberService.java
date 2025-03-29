package shop.sgmarket.sgmarketbackend.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.dto.request.UpdateAddressRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.UpdateAddressResponse;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberUtil memberUtil;

    @Transactional
    public UpdateAddressResponse updateAddress(UpdateAddressRequest updateAddressRequest) {
        Member member = memberUtil.getCurrentMember();
        member.updateAddress(updateAddressRequest.address());

        return UpdateAddressResponse.from(member);
    }
}
