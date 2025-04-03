package shop.sgmarket.sgmarketbackend.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberUpdateResponse;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberUtil memberUtil;

    @Transactional
    public MemberUpdateResponse updateProfile(final MemberUpdateRequest memberUpdateRequest) {
        Member member = memberUtil.getCurrentMember();
        member.updateProfile(memberUpdateRequest.address(), memberUpdateRequest.nickname());

        return MemberUpdateResponse.from(member);
    }
}
