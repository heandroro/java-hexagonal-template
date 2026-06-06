package com.mycompany.template.infra.mariadb.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.mariadb.mapper.UserMariadbMapperImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MariaDBContainer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepositoryAdapterIT.ContainersConfig.class, UserRepositoryAdapter.class, UserMariadbMapperImpl.class})
@Tag("integration")
class UserRepositoryAdapterIT {

    @TestConfiguration(proxyBeanMethods = false)
    static class ContainersConfig {

        @Bean
        @ServiceConnection
        @SuppressWarnings("resource")
        MariaDBContainer<?> mariadb() {
            return new MariaDBContainer<>("mariadb:11-lts")
                    .withReuse(true);
        }
    }

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void should_persistUser_when_saveCalled() {
        var user = buildUser();

        var saved = userRepositoryAdapter.save(user);

        assertThat(saved.id()).isEqualTo(user.id());
        assertThat(saved.name()).isEqualTo(user.name());
        assertThat(saved.email()).isEqualTo(user.email());
        assertThat(saved.createdAt()).isEqualTo(user.createdAt());
    }

    @Test
    void should_findUser_when_idExists() {
        var user = buildUser();
        userRepositoryAdapter.save(user);

        var found = userRepositoryAdapter.findById(user.id());

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(user.id());
        assertThat(found.get().email()).isEqualTo(user.email());
    }

    @Test
    void should_returnEmpty_when_idNotFound() {
        var result = userRepositoryAdapter.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void should_returnTrue_when_emailAlreadyExists() {
        var user = buildUser();
        userRepositoryAdapter.save(user);

        assertThat(userRepositoryAdapter.existsByEmail(user.email())).isTrue();
    }

    @Test
    void should_returnFalse_when_emailNotFound() {
        assertThat(userRepositoryAdapter.existsByEmail("nonexistent@example.com")).isFalse();
    }

    private static User buildUser() {
        return new User(
                UUID.randomUUID(),
                "Test User",
                UUID.randomUUID() + "@example.com",
                LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
        );
    }
}
