package com.mycompany.template.infra.sqs.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.sqs.dto.UserSqsMessage;
import com.mycompany.template.infra.sqs.mapper.UserSqsMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserSqsPublisherTest {

    @Mock
    private SqsTemplate sqsTemplate;

    @Mock
    private UserSqsMapper userSqsMapper;

    @InjectMocks
    private UserSqsPublisher userSqsPublisher;

    @Test
    void should_sendMessageToQueue_when_publishCalled() {
        ReflectionTestUtils.setField(userSqsPublisher, "queueUrl", "user-events-queue");
        var user = Instancio.create(User.class);
        var message = Instancio.create(UserSqsMessage.class);
        given(userSqsMapper.toMessage(user)).willReturn(message);

        userSqsPublisher.publish(user);

        then(sqsTemplate).should().send(eq("user-events-queue"), eq(message));
    }
}
