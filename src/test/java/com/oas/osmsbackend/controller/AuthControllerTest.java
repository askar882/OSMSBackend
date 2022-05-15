package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author askar882
 * @date 2022/04/29
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void login() throws Exception {
        Map<String, String> credentials = Stream.of(new String[][] {
                {"username", "admin"},
                {"password", "admin"}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .build();
        given(userRepository.findByUsername(Mockito.any())).willReturn(Optional.of(admin));
        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.INSTANCE.toJson(credentials)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Login succeed.")));
        verify(userRepository, times(1)).findByUsername(Mockito.any());
        reset(userRepository);
    }
}
