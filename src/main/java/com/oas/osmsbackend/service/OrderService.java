package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Customer;
import com.oas.osmsbackend.entity.Order;
import com.oas.osmsbackend.entity.Product;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.CustomerRepository;
import com.oas.osmsbackend.repository.OrderRepository;
import com.oas.osmsbackend.repository.ProductRepository;
import com.oas.osmsbackend.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * 订单服务。
 *
 * @author askar882
 * @date 2022/05/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public DataResponse create(Order order) {
        Customer customer = order.getCustomer();
        // 只使用唯一值匹配客户。
        customer = customerRepository.findOne(Example.of(Customer.builder()
                .id(customer.getId())
                .phone(customer.getPhone())
                .build()))
                .orElseThrow(() -> new IllegalArgumentException("客户不存在",
                        new ResourceNotFoundException("Customer not found.")));
        order.setCustomer(customer);
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product = productRepository.findOne(Example.of(Product.builder()
                    .id(product.getId())
                    .code(product.getCode())
                    .build()))
                    .orElseThrow(() -> new IllegalArgumentException("产品不存在",
                            new ResourceNotFoundException(("Product doesn't exist."))));
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
        });
        // 计算订单里每种商品的费用并求和算出订单总价，总价四舍五入保留两位小数。
        order.setTotalCost(order.getOrderItems().stream()
                .map(item -> BigDecimal.valueOf(item.getPrice() * item.getCount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue());
        return new DataResponse() {{
            put("order", orderRepository.save(order));
        }};
    }

    /**
     * 通过给定的条件列取数据仓库里的{@link Order}，返回分页的数据。
     * 优先通过{@code optionalCustomers}参数里的Customer ID查询数据仓库。
     * 如果未指定Customer ID，则使用{@code order}查询数据仓库。
     * 如果请求参数不包含{@link Order}类的任何属性，则查询所有数据。
     *
     * @param optionalCustomers 需要匹配的{@link Customer} ID列表。
     * @param order 需要匹配的{@link Order}。
     * @param pageable 分页信息。
     * @return 查询到的商品的 {@link DataResponse}包裹的数据。
     */
    public DataResponse list(Optional<List<Long>> optionalCustomers, Order order, Pageable pageable) {
        log.debug("List orders: customers: '{}', order: '{}', pageable: '{}'.", optionalCustomers, order, pageable);
        Page<Order> orderPage = optionalCustomers.map(customers -> orderRepository.findAllByCustomerIdIn(customers, pageable))
                .orElseGet(() -> orderRepository.findAll(Example.of(order), pageable));
        return new DataResponse() {{
            put("orders", orderPage.getContent());
            put("total", orderPage.getTotalElements());
        }};
    }

    public DataResponse read(Long orderId) {
        return new DataResponse() {{
            put("order", OrderService.this.get(orderId));
        }};
    }

    public DataResponse update(Long orderId, Order order) {
        Order oldOrder = get(orderId);
        if (order.getShipmentTime() != null && oldOrder.getShipmentTime() == null) {
            oldOrder.setShipmentTime(order.getShipmentTime());
        }
        if (order.getDeliveryTime() != null
                && oldOrder.getDeliveryTime() == null) {
            if (oldOrder.getShipmentTime() == null) {
                throw new IllegalStateException("订单尚未开始运输");
            }
            oldOrder.setDeliveryTime(order.getDeliveryTime());
        }
        return new DataResponse() {{
            put("order", orderRepository.save(oldOrder));
        }};
    }

    public void delete(Long orderId) {
        orderRepository.delete(get(orderId));
    }

    public Order get(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ID为 '" + orderId + "' 的订单不存在"));
    }

}
