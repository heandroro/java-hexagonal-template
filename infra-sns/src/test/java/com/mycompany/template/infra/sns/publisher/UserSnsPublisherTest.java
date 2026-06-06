package com.mycompany.template.infra.sns.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.sns.dto.UserSnsNotification;
import com.mycompany.template.infra.sns.mapper.UserSnsMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
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
class UserSnsPublisherTest {

    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private UserSnsMapper userSnsMapper;

    @InjectMocks
    private UserSnsPublisher userSnsPublisher;

    @Test
    void should_sendNotificationToTopic_when_publishCalled() {
        ReflectionTestUtils.setField(userSnsPublisher, "topicArn",
                "arn:aws:sns:us-east-1:000000000000:user-events-topic");
        var user = Instancio.create(User.class);
        var notification = Instancio.create(UserSnsNotification.class);
        given(userSnsMapper.toNotification(user)).willReturn(notification);

        userSnsPublisher.publish(user);

        then(snsTemplate).should().sendNotification(
                eq("arn:aws:sns:us-east-1:000000000000:user-events-topic"),
                eq(notification),
                eq("user.created"));
    }
}
