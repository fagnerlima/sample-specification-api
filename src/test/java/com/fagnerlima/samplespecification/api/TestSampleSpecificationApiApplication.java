package com.fagnerlima.samplespecification.api;

import org.springframework.boot.SpringApplication;

/**
 * Runs the application against a PostgreSQL started by Testcontainers: {@code ./mvnw spring-boot:test-run}.
 */
public class TestSampleSpecificationApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(SampleSpecificationApiApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
