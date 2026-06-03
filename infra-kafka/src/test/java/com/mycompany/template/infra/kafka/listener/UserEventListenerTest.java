package com.mycompany.template.infra.kafka.listener;

import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.infra.kafka.dto.UserEventPayload;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventListenerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private UserEventListener userEventListener;

    @Test
    void onUserCreated_shouldDelegateNameAndEmailToUseCase() {
        var payload = Instancio.create(UserEventPayload.class);

        userEventListener.onUserCreated(payload);

        verify(createUserUseCase).execute(payload.name(), payload.email());
    }
}
