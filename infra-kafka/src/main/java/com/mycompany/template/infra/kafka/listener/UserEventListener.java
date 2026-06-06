package com.mycompany.template.infra.kafka.listener;

import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.infra.kafka.avro.UserEvent;
import com.mycompany.template.infra.kafka.mapper.UserKafkaMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final CreateUserUseCase createUserUseCase;
    private final UserKafkaMapper userKafkaMapper;

    public UserEventListener(CreateUserUseCase createUserUseCase, UserKafkaMapper userKafkaMapper) {
        this.createUserUseCase = createUserUseCase;
        this.userKafkaMapper = userKafkaMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.user-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserCreated(UserEvent event) {
        createUserUseCase.execute(userKafkaMapper.toCommand(event));
    }
}
