package com.oas.osmsbackend.entity;

import com.oas.osmsbackend.entity.embeddable.Address;
import com.oas.osmsbackend.entity.embeddable.OrderItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @NotEmpty
    @Valid
    @Comment("商品清单")
    private Set<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    @NotNull
    @Comment("下单客户")
    private Customer customer;

    @Embedded
    @NotNull
    @Comment("收货地址")
    private Address address;

    @Setter(AccessLevel.NONE)
    @Comment("订单总价")
    private Double totalCost;

    /**
     * 自定义订单总价Getter，计算订单里每种商品的费用并求和算出订单总价。
     *
     * @return 算出的订单总价。
     */
    @SuppressWarnings("unused")
    public Double getTotalCost() {
        return this.orderItems.stream()
                .map(item -> item.getPrice() * item.getCount())
                .mapToDouble(Double::doubleValue)
                .sum();
    }

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
        if (this.orderTime == null) {
            this.orderTime = new Date();
        }
    }
}
