package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.entity.Order;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制器。
 *
 * @author askar882
 * @date 2022/05/19
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "订单控制器", description = "处理管理订单的请求")
public class OrderController {
    private final OrderService orderService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "添加订单")
    public DataResponse create(@RequestBody Order order) {
        return new DataResponse() {{
            put("order", orderService.create(order));
        }};
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "列取订单")
    public DataResponse list() {
        return new DataResponse() {{
            put("orders", orderService.list());
        }};
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "获取订单数据")
    public DataResponse read(@PathVariable Long orderId) {
        return new DataResponse() {{
            put("order", orderService.read(orderId));
        }};
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "更新订单")
    public DataResponse update(@PathVariable Long orderId, @RequestBody Order order) {
        return new DataResponse() {{
            put("order", orderService.update(orderId, order));
        }};
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除订单")
    public void delete(@PathVariable Long orderId) {
        orderService.delete(orderId);
    }
}
