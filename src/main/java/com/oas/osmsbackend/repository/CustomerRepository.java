package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link Customer}数据仓库。
 *
 * @author askar882
 * @date 2022/05/16
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
