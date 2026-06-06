package com.mycompany.template.infra.sns.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserNotificationPublisherPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(UserNotificationPublisherPort.class)
public class NoOpUserNotificationPublisher implements UserNotificationPublisherPort {

    @Override
    public void publish(User user) {
        // no-op: activated when @Profile("sns") is not active
    }
}
