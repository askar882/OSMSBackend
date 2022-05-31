package com.oas.osmsbackend;

import com.oas.osmsbackend.entity.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.security.RedisStore;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 身份认证辅助类。
 *
 * @author askar882
 * @date 2022/05/15
 */
@Slf4j
public class AuthHelper {
    public static final String ADMIN_USER = "admin";
    public static final String NORMAL_USER = "user";

    /**
     * 添加测试用户到传入的{@link UserRepository}实例。
     *
     * @param userRepository  添加用户的{@link UserRepository}实例。
     * @param passwordEncoder 用于加密用户密码的{@link PasswordEncoder}实例。
     */
    public static void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        log.debug("Before user initialization: {}{}.",
                System.lineSeparator(),
                userRepository.findAll().stream()
                        .map(User::toString)
                        .collect(Collectors.joining(System.lineSeparator())));
        log.debug("Initializing users...");
        List<User> users = Arrays.asList(
                User.builder()
                        .id(1L)
                        .username(ADMIN_USER)
                        .password(passwordEncoder.encode(ADMIN_USER))
                        .roles(Stream.of(Role.ADMIN, Role.USER).collect(Collectors.toCollection(HashSet::new)))
                        .build(),
                User.builder()
                        .id(2L)
                        .username(NORMAL_USER)
                        .password(passwordEncoder.encode(NORMAL_USER))
                        .roles(new HashSet<>(Collections.singleton(Role.USER)))
                        .build()
        );
        userRepository.saveAllAndFlush(users);
        log.debug("Printing users...");
        userRepository.findAll().stream().map(User::toString).forEach(log::debug);
    }

    /**
     * 模拟Token验证。
     *
     * @param redisStore 校验Token的{@link RedisStore} Mock对象。
     */
    public static void mockLogin(RedisStore redisStore) {
        Mockito.doAnswer(invocation -> {
            String username = (String) invocation.getArguments()[0];
            Set<Role> roles = new HashSet<>();
            roles.add(Role.USER);
            if (ADMIN_USER.equals(username)) {
                roles.add(Role.ADMIN);
            }
            return Optional.of(User.builder()
                    .username(username)
                    .password(username)
                    .roles(roles)
                    .build());
        }).when(redisStore).getUser(Mockito.any());
    }
}
