package com.oas.osmsbackend;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OsmsBackendApplication.class)
@Slf4j
class OsmsBackendApplicationTests {

    @Test
    void contextLoads() {
        log.debug("Test application context loaded.");
    }

}
