package com.oas.osmsbackend.handler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author askar882
 * @date 2022/04/29
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
public class RestExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRequestBodyMissing() throws Exception {
        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message", containsString("Required request body is missing")))
                .andDo(result -> log.debug(result.getResponse().getContentAsString()));
    }
}
