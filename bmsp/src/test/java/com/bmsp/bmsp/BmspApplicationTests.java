package com.bmsp.bmsp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class BmspApplicationTests {

    @Test
    void contextLoads() {
        // This test will verify that the application context loads successfully
    }
}