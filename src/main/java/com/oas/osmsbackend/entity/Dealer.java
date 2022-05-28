package com.oas.osmsbackend.entity;

import com.oas.osmsbackend.entity.embeddable.ContactPerson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 经销商。
 *
 * @author askar882
 * @date 2022/05/18
 */
@Entity
@Table(name = "dealers")
@org.hibernate.annotations.Table(appliesTo = "dealers", comment = "经销商")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dealer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Comment("经销商名称")
    private String name;

    @Embedded
    @NotNull
    @Valid
    @Comment("联系人")
    private ContactPerson contact;

    @NotBlank
    @Comment("联系电话")
    private String phone;

    @NotBlank
    @Comment("地址")
    private String address;
}
