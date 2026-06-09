package com.mycompany.template.infra.api.rest.mapper;

import com.mycompany.template.core.domain.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserApiMapperTest {

    private final UserApiMapper mapper = new UserApiMapperImpl();

    @Nested
    class ToResponse {

        @Test
        void should_mapAllFields_when_userIsValid() {
            var user = Instancio.create(User.class);

            var response = mapper.toResponse(user);

            assertThat(response.id()).isEqualTo(user.id());
            assertThat(response.name()).isEqualTo(user.name());
            assertThat(response.email()).isEqualTo(user.email());
            assertThat(response.createdAt()).isEqualTo(user.createdAt());
        }

        @Test
        void should_returnNull_when_userIsNull() {
            assertThat(mapper.toResponse(null)).isNull();
        }
    }
}
