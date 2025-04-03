package shop.sgmarket.sgmarketbackend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;


    @Transactional(readOnly = true)
    public Member getCurrentMember() {
        return findMemberOrThrow(securityUtil.getCurrentMemberId());
    }

    @Transactional(readOnly = true)
    public Member getMemberByMemberId(final Long memberId) {
        return findMemberOrThrow(memberId);
    }

    private Member findMemberOrThrow(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public String getMemberRole() {
        String role = securityUtil.getCurrentMemberRole();
        if (role == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        return role;
    }
}
