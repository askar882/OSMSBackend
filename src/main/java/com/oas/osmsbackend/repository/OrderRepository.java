package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * {@link Order}数据仓库。
 *
 * @author askar882
 * @date 2022/05/19
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 通过客户ID列表来匹配订单。
     *
     * @param ids 客户ID列表。
     * @param pageable 分页信息。
     * @return 查询到的分页的商品。
     */
    Page<Order> findAllByCustomerIdIn(Collection<Long> ids, Pageable pageable);
}
