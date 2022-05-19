package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Dealer;
import com.oas.osmsbackend.entity.Product;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.DealerRepository;
import com.oas.osmsbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;

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

    public Product create(Product product) {
        Dealer dealer = product.getDealer();
        // 只使用唯一值匹配经销商。
        dealer = dealerRepository.findOne(Example.of(Dealer.builder()
                        .id(dealer.getId())
                        .name(dealer.getName())
                        .build()))
                .orElseThrow(() -> new IllegalArgumentException("Referenced dealer doesn't exist.",
                        new ResourceNotFoundException("Dealer not found.")));
        product.setDealer(dealer);
        return productRepository.save(product);
    }

    public List<Product> list() {
        return productRepository.findAll();
    }

    public Product read(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found."));
    }

    public Product update(Long productId, Product product) {
        Product oldProduct = read(productId);
        if (product.getCode() != null) {
            if (!oldProduct.getCode().equals(product.getCode())
                    && productRepository.findByCode(product.getCode()).isPresent()) {
                throw new EntityExistsException("Product with code '" + product.getCode() + "' already exists.");
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
        return productRepository.save(oldProduct);
    }

    public void delete(Long productId) {
        productRepository.delete(read(productId));
    }
}