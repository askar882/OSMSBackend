package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Customer;
import com.oas.osmsbackend.entity.Order;
import com.oas.osmsbackend.entity.Product;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.CustomerRepository;
import com.oas.osmsbackend.repository.OrderRepository;
import com.oas.osmsbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Order create(Order order) {
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
        return orderRepository.save(order);
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }

    public Order read(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ID为 '" + orderId + "' 的订单不存在"));
    }

    public Order update(Long orderId, Order order) {
        Order oldOrder = read(orderId);
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
        return orderRepository.save(oldOrder);
    }

    public void delete(Long orderId) {
        orderRepository.delete(read(orderId));
    }
}
