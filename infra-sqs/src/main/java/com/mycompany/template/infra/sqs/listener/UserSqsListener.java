package com.mycompany.template.infra.sqs.listener;

import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.infra.sqs.dto.UserSqsMessage;
import com.mycompany.template.infra.sqs.mapper.UserSqsMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class UserSqsListener {

    private final CreateUserUseCase createUserUseCase;
    private final UserSqsMapper userSqsMapper;

    public UserSqsListener(CreateUserUseCase createUserUseCase, UserSqsMapper userSqsMapper) {
        this.createUserUseCase = createUserUseCase;
        this.userSqsMapper = userSqsMapper;
    }

    @SqsListener("${app.sqs.queues.user-events}")
    public void onMessage(UserSqsMessage message) {
        createUserUseCase.execute(userSqsMapper.toCommand(message));
    }
}
