package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.entity.Order;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 订单控制器。
 *
 * @author askar882
 * @date 2022/05/19
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "订单控制器", description = "处理管理订单的请求")
public class OrderController {
    private final OrderService orderService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "添加订单")
    public DataResponse create(@RequestBody Order order) {
        return orderService.create(order);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "列取订单")
    public DataResponse list(@RequestParam Optional<List<Long>> customers, Optional<Order> optionalOrder, Pageable pageable) {
        return orderService.list(customers, optionalOrder, pageable);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "获取订单数据")
    public DataResponse read(@PathVariable Long orderId) {
        return orderService.read(orderId);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "更新订单")
    public DataResponse update(@PathVariable Long orderId, @RequestBody Order order) {
        return orderService.update(orderId, order);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除订单")
    public void delete(@PathVariable Long orderId) {
        orderService.delete(orderId);
    }
}
