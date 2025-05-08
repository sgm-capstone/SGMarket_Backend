package shop.sgmarket.sgmarketbackend.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(

        @NotBlank(message = "위도는 비어있을 수 없습니다.")
        Double latitude,

        @NotBlank(message = "경도는 비어있을 수 없습니다.")
        Double longitude,

        @NotBlank(message = "주소는 비어있을 수 없습니다.")
        String address,

        @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
        String nickname
) {
}
