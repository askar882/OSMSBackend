package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Order;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }

    public Order read(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID '" + orderId + "' not found."));
    }

    public Order update(Long orderId, Order order) {
        Order oldOrder = read(orderId);
        if (order.getShipmentTime() != null && oldOrder.getShipmentTime() == null) {
            oldOrder.setShipmentTime(order.getShipmentTime());
        }
        if (order.getDeliveryTime() != null
                && oldOrder.getDeliveryTime() == null) {
            if (oldOrder.getShipmentTime() == null) {
                throw new IllegalStateException("Shipment might not have started for this order.");
            }
            oldOrder.setShipmentTime(order.getShipmentTime());
        }
        return orderRepository.save(oldOrder);
    }

    public void delete(Long orderId) {
        orderRepository.delete(read(orderId));
    }
}
