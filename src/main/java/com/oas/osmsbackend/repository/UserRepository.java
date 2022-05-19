package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link User}数据仓库。
 *
 * @author askar882
 * @date 2022/04/25
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 通过用户名称查找用户。
     *
     * @param username 查找的用户名。
     * @return 查到的 {@link User}的{@link Optional}对象。
     */
    Optional<User> findByUsername(String username);
}
