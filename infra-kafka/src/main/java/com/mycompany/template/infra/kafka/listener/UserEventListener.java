package com.mycompany.template.infra.kafka.listener;

import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.infra.kafka.dto.UserEventPayload;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final CreateUserUseCase createUserUseCase;

    public UserEventListener(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @KafkaListener(topics = "${app.kafka.topics.user-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserCreated(UserEventPayload payload) {
        createUserUseCase.execute(payload.name(), payload.email());
    }
}
