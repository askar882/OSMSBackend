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
 * @date 2022/05/02
 */
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ErrorReportTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void test404() throws Exception {
        mockMvc.perform(get("http://localhost:8000/404"))
                .andDo(result -> log.debug("Response: {}", result.getResponse().getContentAsString()))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error.status", is(404)))
                .andExpect(jsonPath("$.error.message", is("Not found")));
    }
}
