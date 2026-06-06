package com.mycompany.template.infra.kafka.listener;

import com.mycompany.template.core.command.CreateUserCommand;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.infra.kafka.avro.UserEvent;
import com.mycompany.template.infra.kafka.mapper.UserKafkaMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserEventListenerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UserKafkaMapper userKafkaMapper;

    @InjectMocks
    private UserEventListener userEventListener;

    @Test
    void should_delegateToUseCaseViaMapper_when_userEventReceived() {
        var event = UserEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Alice")
                .setEmail("alice@example.com")
                .setCreatedAt("2024-01-01T00:00:00")
                .build();
        var command = Instancio.create(CreateUserCommand.class);
        given(userKafkaMapper.toCommand(any(UserEvent.class))).willReturn(command);

        userEventListener.onUserCreated(event);

        then(createUserUseCase).should().execute(command);
    }
}
