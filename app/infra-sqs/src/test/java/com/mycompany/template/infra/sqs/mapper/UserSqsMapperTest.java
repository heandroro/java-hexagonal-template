package com.mycompany.template.infra.sqs.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.sqs.dto.UserSqsMessage;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserSqsMapperTest {

    private final UserSqsMapper mapper = new UserSqsMapperImpl();

    @Nested
    class ToMessage {

        @Test
        void should_mapAllFields_when_userIsValid() {
            var user = Instancio.create(User.class);

            var message = mapper.toMessage(user);

            assertThat(message.id()).isEqualTo(user.id().toString());
            assertThat(message.name()).isEqualTo(user.name());
            assertThat(message.email()).isEqualTo(user.email());
            assertThat(message.createdAt()).isEqualTo(user.createdAt().toString());
        }

        @Test
        void should_returnNull_when_userIsNull() {
            assertThat(mapper.toMessage(null)).isNull();
        }
    }

    @Nested
    class ToCommand {

        @Test
        void should_mapNameAndEmail_when_messageIsValid() {
            var message = Instancio.create(UserSqsMessage.class);

            var command = mapper.toCommand(message);

            assertThat(command.name()).isEqualTo(message.name());
            assertThat(command.email()).isEqualTo(message.email());
        }

        @Test
        void should_returnNull_when_messageIsNull() {
            assertThat(mapper.toCommand(null)).isNull();
        }
    }
}
