package com.oas.osmsbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商品。
 *
 * @author askar882
 * @date 2022/05/16
 */
@Entity
@Table(name = "products")
@org.hibernate.annotations.Table(appliesTo = "products", comment = "商品")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Comment("商品编号")
    private String code;

    @NotBlank
    @Comment("商品名称")
    private String name;

    @NotBlank
    @Comment("商品详情")
    private String description;

    @NotNull
    @Comment("商品价格")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "dealer_id", updatable = false)
    @NotNull
    @Comment("经销商")
    private Dealer dealer;
}
