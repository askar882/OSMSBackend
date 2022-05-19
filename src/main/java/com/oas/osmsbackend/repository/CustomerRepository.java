package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link Customer}数据仓库。
 *
 * @author askar882
 * @date 2022/05/16
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /**
     * 通过手机号查找客户。
     *
     * @param phone 手机号。
     * @return 查到的客户 {@link Customer}的{@link Optional}对象。
     */
    Optional<Customer> findByPhone(String phone);
}
