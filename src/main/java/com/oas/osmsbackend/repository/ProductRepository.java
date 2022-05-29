package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * {@link Product}数据仓库。
 *
 * @author askar882
 * @date 2022/05/19
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * 通过商品编号查找商品。
     *
     * @param code 商品编号。
     * @return 查到的商品 {@link Product}的{@link Optional}对象。
     */
    Optional<Product> findByCode(String code);

    /**
     * 通过经销商ID列表匹配商品。
     *
     * @param ids 经销商ID列表。
     * @param pageable 分页信息。
     * @return 分页的商品。
     */
    Page<Product> findAllByDealerIdIn(Collection<Long> ids, Pageable pageable);
}
