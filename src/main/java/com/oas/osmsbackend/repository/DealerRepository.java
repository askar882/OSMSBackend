package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link Dealer}数据仓库。
 *
 * @author askar882
 * @date 2022/05/19
 */
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    /**
     * 通过名称查找经销商。
     *
     * @param name 经销商名称。
     * @return 查到的经销商 {@link Dealer}的{@link Optional}对象。
     */
    Optional<Dealer> findByName(String name);
}
