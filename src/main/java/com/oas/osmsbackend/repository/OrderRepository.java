package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link Order}数据仓库。
 *
 * @author askar882
 * @date 2022/05/19
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
