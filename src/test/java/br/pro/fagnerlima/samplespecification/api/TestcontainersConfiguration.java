package br.pro.fagnerlima.samplespecification.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * PostgreSQL in Docker for the tests and for {@link TestSampleSpecificationApiApplication}. Flyway applies the real
 * migrations, including the {@code unaccent} extension and the seed data.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgreSqlContainer() {
        return new PostgreSQLContainer("postgres:17-alpine");
    }

}
