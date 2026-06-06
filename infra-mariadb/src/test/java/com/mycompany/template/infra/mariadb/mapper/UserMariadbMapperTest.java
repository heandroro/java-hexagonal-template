package com.mycompany.template.infra.mariadb.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.mariadb.entity.UserEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMariadbMapperTest {

    private final UserMariadbMapper mapper = new UserMariadbMapperImpl();

    @Test
    void toEntity_shouldMapAllFieldsFromDomain() {
        var user = Instancio.create(User.class);
        var entity = mapper.toEntity(user);

        assertThat(entity.getId()).isEqualTo(user.id());
        assertThat(entity.getName()).isEqualTo(user.name());
        assertThat(entity.getEmail()).isEqualTo(user.email());
        assertThat(entity.getCreatedAt()).isEqualTo(user.createdAt());
    }

    @Test
    void toDomain_shouldMapAllFieldsFromEntity() {
        var entity = Instancio.create(UserEntity.class);
        var user = mapper.toDomain(entity);

        assertThat(user.id()).isEqualTo(entity.getId());
        assertThat(user.name()).isEqualTo(entity.getName());
        assertThat(user.email()).isEqualTo(entity.getEmail());
        assertThat(user.createdAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void roundTrip_toEntityThenToDomain_shouldPreserveAllFields() {
        var original = Instancio.create(User.class);

        var result = mapper.toDomain(mapper.toEntity(original));

        assertThat(result).isEqualTo(original);
    }
}
