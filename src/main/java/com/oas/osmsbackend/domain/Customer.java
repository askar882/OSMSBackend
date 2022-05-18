package com.oas.osmsbackend.domain;

import com.oas.osmsbackend.domain.embeddable.Address;
import com.oas.osmsbackend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * 客户。
 *
 * @author askar882
 * @date 2022/05/16
 */
@Entity
@Table(name = "customers")
@org.hibernate.annotations.Table(appliesTo = "customers", comment = "客户")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Comment("姓名")
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Comment("性别")
    private Gender gender;

    @Comment("邮箱")
    private String email;

    @Comment("电话")
    private String phone;

    @Comment("生日")
    private Date birthDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Comment("地址")
    private Set<Address> addresses;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @Comment("订单")
    private Set<Order> orders;
}
