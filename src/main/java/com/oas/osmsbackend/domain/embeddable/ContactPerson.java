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
 * 联系人信息。
 *
 * @author askar882
 * @date 2022/05/18
 */
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "contact_name")),
        @AttributeOverride(name = "phone", column = @Column(name = "contact_phone")),
        @AttributeOverride(name = "email", column = @Column(name = "contact_email"))
})
@Getter
@Setter
@ToString
public class ContactPerson {
    @NotNull
    @Comment("联系人姓名")
    private String name;

    @NotNull
    @Comment("联系人电话")
    private String phone;

    @Comment("联系人邮箱")
    private String email;
}
