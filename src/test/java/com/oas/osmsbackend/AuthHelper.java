package com.oas.osmsbackend;

import com.oas.osmsbackend.domain.User;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author askar882
 * @date 2022/05/15
 */
@Slf4j
public class AuthHelper {
    public static void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        log.debug("Initializing users...");
        List<User> users = Arrays.asList(
                User.builder()
                        .id(1L)
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
                        .build(),
                User.builder()
                        .id(2L)
                        .username("user")
                        .password(passwordEncoder.encode("user"))
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build()
        );
        userRepository.saveAllAndFlush(users);
//        given(userRepository.findAll()).willReturn(users);
//        users.forEach(user -> {
//                given(userRepository.findByUsername(user.getUsername()))
//                        .willReturn(Optional.of(user));
//        });
    }

    public static Optional<String> mockLogin(MockMvc mockMvc, String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<String, String>() {{
            put("username", username);
            put("password", password);
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
