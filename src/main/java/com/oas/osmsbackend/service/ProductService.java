package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Dealer;
import com.oas.osmsbackend.entity.Product;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.DealerRepository;
import com.oas.osmsbackend.repository.ProductRepository;
import com.oas.osmsbackend.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

/**
 * 商品服务。
 *
 * @author askar882
 * @date 2022/05/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final DealerRepository dealerRepository;

    public DataResponse create(Product product) {
        Dealer dealer = product.getDealer();
        // 只使用唯一值匹配经销商。
        dealer = dealerRepository.findOne(Example.of(Dealer.builder()
                        .id(dealer.getId())
                        .name(dealer.getName())
                        .build()))
                .orElseThrow(() -> new IllegalArgumentException("经销商不存在",
                        new ResourceNotFoundException("Dealer not found.")));
        product.setDealer(dealer);
        return new DataResponse() {{
            put("product", productRepository.save(product));
        }};
    }

    public DataResponse list(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return new DataResponse() {{
            put("products", productPage.getContent());
            put("total", productPage.getTotalElements());
        }};
    }

    public DataResponse read(Long productId) {
        return new DataResponse() {{
            put("product", ProductService.this.get(productId));
        }};
    }

    public DataResponse update(Long productId, Product product) {
        Product oldProduct = get(productId);
        if (product.getCode() != null) {
            if (!oldProduct.getCode().equals(product.getCode())
                    && productRepository.findByCode(product.getCode()).isPresent()) {
                throw new EntityExistsException("代码为 '" + product.getCode() + "' 的产品已存在");
            }
            oldProduct.setCode(product.getCode());
        }
        if (product.getName() != null) {
            oldProduct.setName(product.getName());
        }
        if (product.getDescription() != null) {
            oldProduct.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            oldProduct.setPrice(product.getPrice());
        }
        return new DataResponse() {{
            put("product", productRepository.save(oldProduct));
        }};
    }

    public void delete(Long productId) {
        productRepository.delete(get(productId));
    }

    public Product get(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("ID为 '" + productId + "' 的产品不存在"));
    }
}
