package com.mycompany.template.infra.sns.publisher;

import com.mycompany.template.core.domain.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class NoOpUserNotificationPublisherTest {

    private final NoOpUserNotificationPublisher publisher = new NoOpUserNotificationPublisher();

    @Test
    void should_doNothing_when_publishCalled() {
        User user = Instancio.create(User.class);

        assertThatNoException().isThrownBy(() -> publisher.publish(user));
    }
}
