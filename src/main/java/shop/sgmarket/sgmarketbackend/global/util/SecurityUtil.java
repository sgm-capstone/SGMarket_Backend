package shop.sgmarket.sgmarketbackend.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;

@Component
public class SecurityUtil {

    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    public String getCurrentMemberRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("UNKNOWN");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

}
