package com.mycompany.template.infra.sqs.listener;

import com.mycompany.template.core.command.CreateUserCommand;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.infra.sqs.dto.UserSqsMessage;
import com.mycompany.template.infra.sqs.mapper.UserSqsMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserSqsListenerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UserSqsMapper userSqsMapper;

    @InjectMocks
    private UserSqsListener userSqsListener;

    @Test
    void should_delegateToUseCaseViaMapper_when_sqsMessageReceived() {
        var message = Instancio.create(UserSqsMessage.class);
        var command = Instancio.create(CreateUserCommand.class);
        given(userSqsMapper.toCommand(message)).willReturn(command);

        userSqsListener.onMessage(message);

        then(createUserUseCase).should().execute(command);
    }
}
