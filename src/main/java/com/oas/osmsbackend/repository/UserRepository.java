package com.oas.osmsbackend.repository;

import com.oas.osmsbackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author askar882
 * @date 2022/04/25
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by username.
     * @param username Username to match.
     * @return An {#Optional<User>} instance
     */
    Optional<User> findByUsername(String username);
}
