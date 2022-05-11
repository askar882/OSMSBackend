package com.oas.osmsbackend.service;

import com.oas.osmsbackend.domain.AuthenticationRequest;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

/**
 * @author askar882
 * @date 2022/05/01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(AuthenticationRequest authenticationRequest) {
        userRepository.findByUsername(authenticationRequest.getUsername()).ifPresent((user) -> {
            throw new EntityExistsException("User '".concat(user.getUsername()).concat("' already exists."));
        });
        User user = User.builder()
                .username(authenticationRequest.getUsername())
                .password(passwordEncoder.encode(authenticationRequest.getPassword()))
                .build();
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
