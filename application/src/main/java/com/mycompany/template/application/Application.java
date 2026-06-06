package com.mycompany.template.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.mycompany.template")
@EnableFeignClients(basePackages = "com.mycompany.template.infra.client")
@EntityScan(basePackages = "com.mycompany.template")
@EnableJpaRepositories(basePackages = "com.mycompany.template")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
