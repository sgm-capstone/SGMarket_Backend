package shop.sgmarket.sgmarketbackend.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.notification.domain.Notification;
import shop.sgmarket.sgmarketbackend.notification.repository.NotificationRepository;

/**
 * 알림 조회 및 읽음 처리 전용 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final MemberUtil memberUtil;

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByMemberId(Pageable pageable) {
        Member member = memberUtil.getCurrentMember();
        return notificationRepository.findByMemberOrderByCreatedAtDesc(member, pageable);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. id=" + notificationId));
        notification.markAsRead();
    }
}
