package shop.sgmarket.sgmarketbackend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLocation {

    private String address;

    @Column(nullable = false, precision = 9, scale = 6)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
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
