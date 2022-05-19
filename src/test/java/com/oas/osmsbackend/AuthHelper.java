package com.oas.osmsbackend;

import com.oas.osmsbackend.entity.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
     * 执行登录操作并返回获取的token。
     *
     * @param mockMvc  执行登录的{@link MockMvc}实例。
     * @param username 登录的用户名。
     * @return 获取的token。
     */
    public static Optional<String> mockLogin(MockMvc mockMvc, String username) throws Exception {
        Map<String, String> credentials = new HashMap<String, String>() {{
            put("username", username);
            put("password", username);
        }};
        log.debug("Perform login with mock MVC with credentials: '{}'.", credentials);
        return JsonUtil.INSTANCE.fromJson(
                        mockMvc.perform(
                                        post("/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding(StandardCharsets.UTF_8)
                                                .content(JsonUtil.INSTANCE.toJson(credentials, false)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString(),
                        DataResponse.class,
                        true)
                .map(data -> (String) data.get("token"));
    }
}
