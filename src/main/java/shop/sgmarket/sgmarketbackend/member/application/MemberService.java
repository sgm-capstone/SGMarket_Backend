package shop.sgmarket.sgmarketbackend.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.domain.MemberLocation;
import shop.sgmarket.sgmarketbackend.member.dto.request.MemberUpdateRequest;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberInfoResponse;
import shop.sgmarket.sgmarketbackend.member.dto.response.MemberUpdateResponse;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberUtil memberUtil;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfoFromId() {
        Member member = memberUtil.getCurrentMember();

        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberUpdateResponse updateProfile(final MemberUpdateRequest memberUpdateRequest) {
        Member member = memberUtil.getCurrentMember();
        MemberLocation memberLocation = MemberLocation.createMemberLocation(
                memberUpdateRequest.address(),
                memberUpdateRequest.latitude(),
                memberUpdateRequest.longitude()
        );

        member.updateProfile(memberLocation, memberUpdateRequest.nickname());

        return MemberUpdateResponse.from(member);
    }
}
