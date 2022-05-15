package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.AuthHelper;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author askar882
 * @date 2022/05/12
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void initUsers() {
        AuthHelper.initUsers(userRepository, passwordEncoder);
        log.debug("Printing users...");
        userRepository.findAll().stream().map(User::toString).forEach(log::debug);
    }

    @Test
    public void testList() throws Exception {
        log.debug("Testing list user query with user 'user'.");
        String token = AuthHelper.mockLogin(mockMvc, "user", "user").orElseThrow(RuntimeException::new);
        mockMvc.perform(
                get("/users")
                        .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER_TOKEN + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.error.message", is("Unauthorized role.")));
        log.debug("Testing list user query with user 'admin'.");
        token = AuthHelper.mockLogin(mockMvc, "admin", "admin").orElseThrow(RuntimeException::new);
        mockMvc.perform(
                        get("/users")
                                .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER_TOKEN + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users", isA(List.class)))
                .andDo(handler -> log.debug("Users list response: {}.", handler.getResponse().getContentAsString()));
    }
}
