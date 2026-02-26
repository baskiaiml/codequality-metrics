package com.vishvakta.example.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.vishvakta.example.metrics")
public class CodeQualityMetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeQualityMetricsApplication.class, args);
    }
}
