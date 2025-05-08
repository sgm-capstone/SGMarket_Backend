package shop.sgmarket.sgmarketbackend.member.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLocation {
    private String address;
    private Double latitude;
    private Double longitude;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberLocation(String address, Double latitude, Double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static MemberLocation createMemberLocation(final String address, final Double latitude, final Double longitude) {
        return MemberLocation.builder()
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
