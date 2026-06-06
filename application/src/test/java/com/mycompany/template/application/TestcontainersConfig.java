package com.mycompany.template.application;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:17-alpine")
                .withReuse(true);
    }

    @Bean
    @ServiceConnection("redis")
    @SuppressWarnings("resource")
    GenericContainer<?> valkey() {
        return new GenericContainer<>("valkey/valkey:8-alpine")
                .withExposedPorts(6379)
                .withReuse(true);
    }

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    KafkaContainer kafka() {
        return new KafkaContainer("apache/kafka:3.7.1")
                .withReuse(true);
    }
}
