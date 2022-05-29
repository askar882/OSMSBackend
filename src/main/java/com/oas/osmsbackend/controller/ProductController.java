package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.entity.Product;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.ProductService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品控制器。
 *
 * @author askar882
 * @date 2022/05/19
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "商品控制器", description = "处理管理商品的请求")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "添加商品")
    public DataResponse create(@RequestBody Product product) {
        return productService.create(product);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "列取商品")
    public DataResponse list(Pageable pageable) {
        return productService.list(pageable);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "获取商品数据")
    public DataResponse read(@PathVariable Long productId) {
        return productService.read(productId);
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "更新商品")
    public DataResponse update(@PathVariable Long productId, @RequestBody Product product) {
        return productService.update(productId, product);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除商品")
    public void delete(@PathVariable Long productId) {
        productService.delete(productId);
    }
}
