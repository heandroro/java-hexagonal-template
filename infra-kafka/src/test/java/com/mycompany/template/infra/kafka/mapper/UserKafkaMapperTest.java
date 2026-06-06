package com.mycompany.template.infra.kafka.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.kafka.avro.UserEvent;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserKafkaMapperTest {

    private final UserKafkaMapper mapper = new UserKafkaMapperImpl();

    @Nested
    class ToEvent {

        @Test
        void should_mapAllFields_when_userIsValid() {
            var user = Instancio.create(User.class);

            var event = mapper.toEvent(user);

            assertThat(event.getId()).isEqualTo(user.id().toString());
            assertThat(event.getName()).isEqualTo(user.name());
            assertThat(event.getEmail()).isEqualTo(user.email());
            assertThat(event.getCreatedAt()).isEqualTo(user.createdAt().toString());
        }

        @Test
        void should_returnNull_when_userIsNull() {
            assertThat(mapper.toEvent(null)).isNull();
        }
    }

    @Nested
    class ToCommand {

        @Test
        void should_mapNameAndEmail_when_eventIsValid() {
            var event = UserEvent.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("Alice")
                    .setEmail("alice@example.com")
                    .setCreatedAt("2024-01-01T00:00:00")
                    .build();

            var command = mapper.toCommand(event);

            assertThat(command.name()).isEqualTo(event.getName());
            assertThat(command.email()).isEqualTo(event.getEmail());
        }

        @Test
        void should_returnNull_when_eventIsNull() {
            assertThat(mapper.toCommand(null)).isNull();
        }
    }
}
