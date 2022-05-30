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
import java.util.List;
import java.util.Optional;

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

    /**
     * 通过给定的条件列取数据仓库里的{@link Product}，返回分页的数据。
     * 优先通过{@code optionalDealers}参数里的Dealer ID查询数据仓库。
     * 如果未指定Dealer ID，则使用{@code product}查询数据仓库。
     * 如果请求参数不包含{@link Product}类的任何属性，则查询所有数据。
     *
     * @param optionalDealers 需要匹配的{@link Dealer} ID列表。
     * @param product         需要匹配的{@link Product}。
     * @param pageable        分页信息。
     * @return 查询到的商品的 {@link DataResponse}包裹的数据。
     */
    public DataResponse list(Optional<List<Long>> optionalDealers, Product product, Pageable pageable) {
        log.debug("List products: optionalDealers: '{}', product: '{}', pageable: '{}'.", optionalDealers, product, pageable);
        Page<Product> productPage = optionalDealers.map(dealers -> productRepository.findAllByDealerIdIn(dealers, pageable))
                .orElseGet(() -> productRepository.findAll(Example.of(product), pageable));
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
