package com.oas.osmsbackend.domain.embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * 地址。
 *
 * @author askar882
 * @date 2022/05/18
 */
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "province", column = @Column(name = "address_province")),
        @AttributeOverride(name = "city", column = @Column(name = "address_city")),
        @AttributeOverride(name = "district", column = @Column(name = "address_district")),
        @AttributeOverride(name = "street", column = @Column(name = "address_street")),
        @AttributeOverride(name = "detail", column = @Column(name = "address_detail"))
})
@Getter
@Setter
@ToString
public class Address {
    @NotNull
    @Comment("省份/地区")
    private String province;

    @NotNull
    @Comment("城市")
    private String city;

    @NotNull
    @Comment("区/县")
    private String district;

    @NotNull
    @Comment("街道/镇")
    private String street;

    @NotNull
    @Comment("详细地址")
    private String detail;

    @NotNull
    @Comment("邮编")
    private String zip;
}
