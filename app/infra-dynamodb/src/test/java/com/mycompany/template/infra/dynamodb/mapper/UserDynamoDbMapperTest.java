package com.mycompany.template.infra.dynamodb.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.dynamodb.entity.UserDynamoDbEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserDynamoDbMapperTest {

    private final UserDynamoDbMapper mapper = new UserDynamoDbMapperImpl();

    @Test
    void toEntity_shouldMapAllFields_andStoreCreatedAtAsIsoString() {
        var user = Instancio.create(User.class);
        var entity = mapper.toEntity(user);

        assertThat(entity.getId()).isEqualTo(user.id());
        assertThat(entity.getName()).isEqualTo(user.name());
        assertThat(entity.getEmail()).isEqualTo(user.email());
        assertThat(entity.getCreatedAt()).isEqualTo(user.createdAt().toString());
    }

    @Test
    void toDomain_shouldMapAllFields_andParseCreatedAtFromIsoString() {
        var entity = new UserDynamoDbEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setName("Jane Doe");
        entity.setEmail("jane@example.com");
        entity.setCreatedAt(LocalDateTime.of(2024, 6, 1, 12, 0).toString());

        var user = mapper.toDomain(entity);

        assertThat(user.id()).isEqualTo(entity.getId());
        assertThat(user.name()).isEqualTo(entity.getName());
        assertThat(user.email()).isEqualTo(entity.getEmail());
        assertThat(user.createdAt()).isEqualTo(LocalDateTime.of(2024, 6, 1, 12, 0));
    }

    @Test
    void toEntity_shouldHandleNullCreatedAt() {
        var user = new User(java.util.UUID.randomUUID(), "John", "john@example.com", null);
        var entity = mapper.toEntity(user);
        assertThat(entity.getCreatedAt()).isNull();
    }

    @Test
    void toDomain_shouldHandleNullCreatedAt() {
        var entity = new UserDynamoDbEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setName("John");
        entity.setEmail("john@example.com");
        entity.setCreatedAt(null);

        var user = mapper.toDomain(entity);

        assertThat(user.createdAt()).isNull();
    }
}
