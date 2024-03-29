package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.security.RedisStore;
import com.oas.osmsbackend.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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
    private final RedisStore redisStore;

    public DataResponse create(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new EntityExistsException("用户 '" + user.getUsername() + "' 已存在");
        });
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>(Collections.singleton(Role.USER)));
        }
        log.debug("Registered user {}.", user);
        return new DataResponse() {{
            put("user", userRepository.save(user));
        }};
    }

    public DataResponse list(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return new DataResponse() {{
            put("users", userPage.getContent());
            put("total", userPage.getTotalElements());
            put("online", redisStore.listIds());
        }};
    }

    public DataResponse read(Long userId) throws AccountNotFoundException {
        return new DataResponse() {{
            put("user", UserService.this.get(userId));
        }};
    }

    public DataResponse update(Long userId, User user) throws AccountNotFoundException {
        User oldUser = get(userId);
        if (user.getUsername() != null) {
            if (!oldUser.getUsername().equals(user.getUsername())
                    && userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new EntityExistsException("用户 '" + user.getUsername() + "' 已存在");
            }
            oldUser.setUsername(user.getUsername());
        }
        if (StringUtils.hasText(user.getPassword())) {
            oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (RequestUtil.INSTANCE.currentUser().getRoles().contains(Role.ADMIN)) {
            Set<Role> roles = user.getRoles();
            if (roles == null) {
                roles = oldUser.getRoles();
            }
            roles.add(Role.USER);
            oldUser.setRoles(roles);
            oldUser.setEnabled(user.getEnabled());
            oldUser.setModificationTime(new Date());
        }
        User newUser = userRepository.save(oldUser);
        redisStore.deleteToken(oldUser.getId());
        return new DataResponse() {{
            put("user", newUser);
        }};
    }

    public void delete(Long userId) throws AccountNotFoundException {
        if (RequestUtil.INSTANCE.currentUser().getId().equals(userId)) {
            throw new IllegalStateException("无法删除当前登录的用户");
        }
        User user = get(userId);
        userRepository.delete(user);
        redisStore.deleteToken(userId);
    }

    public User get(Long userId) throws AccountNotFoundException {
        return userRepository.findById(userId).orElseThrow(
                () -> new AccountNotFoundException("ID为 '" + userId + "' 的用户不存在"));
    }
}
