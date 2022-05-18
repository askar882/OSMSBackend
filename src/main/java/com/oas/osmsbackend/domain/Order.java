package com.oas.osmsbackend.domain;

import com.oas.osmsbackend.domain.embeddable.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * 订单。
 *
 * @author askar882
 * @date 2022/05/18
 */
@Entity
@Table(name = "orders")
@org.hibernate.annotations.Table(appliesTo = "orders", comment = "订单")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    @Comment("订单里的商品")
    private Set<Product> products;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Comment("下单客户")
    private User user;

    @NotNull
    @Embedded
    @Comment("收货地址")
    private Address address;

    @NotNull
    @Comment("订单总价")
    private Double totalCost;

    @Builder.Default
    @Comment("运费")
    private Double shippingCost = 0.0;

    @Comment("下单时间")
    private Date orderTime;

    @Comment("发货时间")
    private Date shipmentTime;

    @Comment("收货时间")
    private Date deliveryTime;


    @PrePersist
    protected void prePersist() {
        if (orderTime == null) {
            orderTime = new Date();
        }
    }
}
