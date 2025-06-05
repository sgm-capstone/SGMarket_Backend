package shop.sgmarket.sgmarketbackend.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;
import shop.sgmarket.sgmarketbackend.notification.domain.Notification;
import shop.sgmarket.sgmarketbackend.notification.dto.NotificationInfoResponse;
import shop.sgmarket.sgmarketbackend.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final MemberUtil memberUtil;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public SliceResponse<NotificationInfoResponse> getNotificationsByMemberId(Pageable pageable) {
        Member member = memberUtil.getCurrentMember();
        Slice<Notification> notifications = notificationRepository.findByMemberOrderByCreatedAtDesc(member, pageable);
        return SliceResponse.from(notifications.map(NotificationInfoResponse::from));
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Member member = memberUtil.getCurrentMember();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getMember().equals(member)) {
            throw new CustomException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        notification.markAsRead();
    }
}
