package com.oas.osmsbackend.service;

import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

/**
 * 用户服务类。
 *
 * @author askar882
 * @date 2022/05/01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent((u) -> {
            throw new EntityExistsException("User '" + user.getUsername() + "' already exists.");
        });
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Registered user {}.", user);
        return userRepository.saveAndFlush(user);
    }

    public User updateUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    public User patchUser(User user) {
        return userRepository.saveAndFlush(user);
    }
}
