package com.oas.osmsbackend.domain.embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

/**
 * 地址。
 *
 * @author askar882
 * @date 2022/05/18
 */
@Embeddable
@Getter
@Setter
@ToString
public class Address {
    @NotBlank
    @Comment("省份/地区")
    private String province;

    @NotBlank
    @Comment("城市")
    private String city;

    @NotBlank
    @Comment("区/县")
    private String district;

    @NotBlank
    @Comment("街道/镇")
    private String street;

    @NotBlank
    @Comment("详细地址")
    private String address;

    @NotBlank
    @Comment("邮编")
    private String zip;
}
