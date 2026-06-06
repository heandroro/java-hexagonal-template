package com.mycompany.template.infra.sqs.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserQueuePublisherPort;
import com.mycompany.template.infra.sqs.dto.UserSqsMessage;
import com.mycompany.template.infra.sqs.mapper.UserSqsMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("sqs")
public class UserSqsPublisher implements UserQueuePublisherPort {

    private final SqsTemplate sqsTemplate;
    private final UserSqsMapper userSqsMapper;

    @Value("${app.sqs.queues.user-events}")
    private String queueUrl;

    public UserSqsPublisher(SqsTemplate sqsTemplate, UserSqsMapper userSqsMapper) {
        this.sqsTemplate = sqsTemplate;
        this.userSqsMapper = userSqsMapper;
    }

    @Override
    public void publish(User user) {
        UserSqsMessage message = userSqsMapper.toMessage(user);
        sqsTemplate.send(queueUrl, message);
    }
}
