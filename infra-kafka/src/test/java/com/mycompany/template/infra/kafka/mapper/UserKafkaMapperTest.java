package com.mycompany.template.infra.kafka.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.kafka.dto.UserEventPayload;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserKafkaMapperTest {

    private final UserKafkaMapper mapper = new UserKafkaMapperImpl();

    @Nested
    class ToDomain {

        @Test
        void should_mapAllFields_when_payloadIsValid() {
            var payload = Instancio.create(UserEventPayload.class);

            var user = mapper.toDomain(payload);

            assertThat(user.id()).isEqualTo(payload.id());
            assertThat(user.name()).isEqualTo(payload.name());
            assertThat(user.email()).isEqualTo(payload.email());
            assertThat(user.createdAt()).isEqualTo(payload.createdAt());
        }

        @Test
        void should_returnNull_when_payloadIsNull() {
            assertThat(mapper.toDomain(null)).isNull();
        }
    }

    @Nested
    class ToPayload {

        @Test
        void should_mapAllFields_when_userIsValid() {
            var user = Instancio.create(User.class);

            var payload = mapper.toPayload(user);

            assertThat(payload.id()).isEqualTo(user.id());
            assertThat(payload.name()).isEqualTo(user.name());
            assertThat(payload.email()).isEqualTo(user.email());
            assertThat(payload.createdAt()).isEqualTo(user.createdAt());
        }

        @Test
        void should_returnNull_when_userIsNull() {
            assertThat(mapper.toPayload(null)).isNull();
        }
    }

    @Test
    void should_preserveAllFields_when_roundTripToPayloadThenToDomain() {
        var original = Instancio.create(User.class);

        var result = mapper.toDomain(mapper.toPayload(original));

        assertThat(result).isEqualTo(original);
    }
}
