package com.oas.osmsbackend.service;

import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public User create(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent((u) -> {
            throw new EntityExistsException("User '" + user.getUsername() + "' already exists.");
        });
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Registered user {}.", user);
        return userRepository.save(user);
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public User read(Long userId) throws AccountNotFoundException {
        return userRepository.findById(userId).orElseThrow(
                () -> new AccountNotFoundException("User with ID '" + userId + "' is not found."));
    }

    public User update(Long userId, User user) throws AccountNotFoundException {
        User oldUser = read(userId);
        if (user.getUsername() != null) {
            if (!oldUser.getUsername().equals(user.getUsername())
                    && userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new EntityExistsException("User '" + user.getUsername() + "' already exists.");
            }
            oldUser.setUsername(user.getUsername());
        }
        if (StringUtils.hasText(user.getPassword())) {
            oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (RequestUtil.INSTANCE.currentUser().getRoles().contains(Role.ADMIN)) {
            Set<Role> roles = user.getRoles();
            if (roles == null) {
                roles = new HashSet<>();
            }
            roles.add(Role.USER);
            user.setRoles(roles);
        } else {
            user.setRoles(oldUser.getRoles());
        }
        user.setId(oldUser.getId());
        return userRepository.save(user);
    }


    public void delete(Long userId) throws AccountNotFoundException {
        if (RequestUtil.INSTANCE.currentUser().getId().equals(userId)) {
            throw new IllegalStateException("Cannot perform delete action on self.");
        }
        userRepository.delete(read(userId));
    }
}
