package com.mycompany.template.infra.sqs.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserQueuePublisherPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(UserQueuePublisherPort.class)
public class NoOpUserQueuePublisher implements UserQueuePublisherPort {

    @Override
    public void publish(User user) {
        // no-op: activated when @Profile("sqs") is not active
    }
}
