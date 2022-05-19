package com.oas.osmsbackend.domain.embeddable;

import com.oas.osmsbackend.domain.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * 单个商品清单。
 *
 * @author askar882
 * @date 2022/05/18
 */
@Embeddable
@Getter
@Setter
@ToString
public class OrderItem {
    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull
    @Comment("购买的商品")
    private Product product;

    @NotNull
    @Comment("购买数量")
    private Integer count;
}
