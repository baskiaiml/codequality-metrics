package com.vishvakta.example.metrics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CodeQualityMetricsApplicationTest {

    @Test
    void contextLoads() {
        assertNotNull(CodeQualityMetricsApplication.class);
    }
}
