package shop.sgmarket.sgmarketbackend.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberRegisterRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberRegisterResponse;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberUtil memberUtil;

    @Transactional
    public MemberRegisterResponse updateAddress(MemberRegisterRequest memberRegisterRequest) {
        Member member = memberUtil.getCurrentMember();
        member.register(memberRegisterRequest.address(), memberRegisterRequest.nickname());

        return MemberRegisterResponse.from(member);
    }
}
