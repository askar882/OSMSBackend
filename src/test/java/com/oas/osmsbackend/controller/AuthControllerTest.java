package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.entity.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.security.RedisStore;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @MockBean
    private RedisStore redisStore;

    @Test
    public void login() throws Exception {
        Map<String, String> credentials = Stream.of(new String[][] {
                {"username", "admin"},
                {"password", "admin"}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(new HashSet<>(Arrays.asList(Role.ADMIN, Role.USER)))
                .build();
        given(userRepository.findByUsername(Mockito.any())).willReturn(Optional.of(admin));
        Map<String, Object> localStore = new HashMap<>();
        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            String token = (String) args[1];
            localStore.put(token, args[0]);
            log.debug("Mock saveToken: '{}'.", (Object) args);
            return null;
        }).when(redisStore).saveToken(Mockito.any(), Mockito.any());
        log.debug("localStore: '{}'.", localStore);
        when(redisStore.getUser(Mockito.any()))
                .thenAnswer(invocation ->
                        localStore.get((String) invocation.getArgument(0)));
        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.INSTANCE.toJson(credentials, false)))
                .andExpect(status().isCreated());
        verify(userRepository, times(1)).findByUsername(Mockito.any());
        reset(userRepository);
    }
}
