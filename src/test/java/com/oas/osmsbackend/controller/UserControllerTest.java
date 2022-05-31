package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.AuthHelper;
import com.oas.osmsbackend.config.AppConfiguration;
import com.oas.osmsbackend.security.RedisStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试。
 *
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
    private AppConfiguration appConfiguration;

    @MockBean
    private RedisStore redisStore;


    @Test
    public void testList() throws Exception {
        log.debug("Testing list user query with user 'user'.");
        AuthHelper.mockLogin(redisStore);
        mockMvc.perform(
                get("/users")
                        .header(appConfiguration.getAuthHeader(),
                                appConfiguration.getBearerToken() + AuthHelper.NORMAL_USER))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.error.message", is("未授权的角色")));
        log.debug("Testing list user query with user 'admin'.");
        mockMvc.perform(
                        get("/users")
                                .header(appConfiguration.getAuthHeader(),
                                        appConfiguration.getBearerToken() + AuthHelper.ADMIN_USER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users", isA(List.class)))
                .andDo(handler -> log.debug("Users list response: {}.", handler.getResponse().getContentAsString()));
    }
}
