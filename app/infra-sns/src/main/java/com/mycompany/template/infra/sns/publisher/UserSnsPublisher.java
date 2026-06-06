package com.mycompany.template.infra.sns.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserNotificationPublisherPort;
import com.mycompany.template.infra.sns.dto.UserSnsNotification;
import com.mycompany.template.infra.sns.mapper.UserSnsMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("sns")
public class UserSnsPublisher implements UserNotificationPublisherPort {

    private final SnsTemplate snsTemplate;
    private final UserSnsMapper userSnsMapper;

    @Value("${app.sns.topics.user-events}")
    private String topicArn;

    public UserSnsPublisher(SnsTemplate snsTemplate, UserSnsMapper userSnsMapper) {
        this.snsTemplate = snsTemplate;
        this.userSnsMapper = userSnsMapper;
    }

    @Override
    public void publish(User user) {
        UserSnsNotification notification = userSnsMapper.toNotification(user);
        snsTemplate.sendNotification(topicArn, notification, "user.created");
    }
}
