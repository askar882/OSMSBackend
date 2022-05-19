package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.domain.Customer;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.CustomerService;
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
 * 客户控制器。
 *
 * @author askar882
 * @date 2022/05/16
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "客户控制器", description = "处理管理客户的请求")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "添加客户")
    public DataResponse create(@RequestBody Customer customer) {
        return new DataResponse() {{
            put("customer", customerService.create(customer));
        }};
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse list() {
        return new DataResponse() {{
            put("customers", customerService.list());
        }};
    }

    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse read(@PathVariable Long customerId) {
        return new DataResponse() {{
            put("customer", customerService.read(customerId));
        }};
    }

    @PutMapping("/{customerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DataResponse update(@PathVariable Long customerId, @RequestBody Customer customer) {
        return new DataResponse() {{
            put("customer", customerService.update(customerId, customer));
        }};
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long customerId) {
        customerService.delete(customerId);
    }
}
