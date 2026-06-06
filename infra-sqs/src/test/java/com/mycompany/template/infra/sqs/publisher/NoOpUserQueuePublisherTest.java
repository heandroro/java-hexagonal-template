package com.mycompany.template.infra.sqs.publisher;

import com.mycompany.template.core.domain.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class NoOpUserQueuePublisherTest {

    private final NoOpUserQueuePublisher publisher = new NoOpUserQueuePublisher();

    @Test
    void should_doNothing_when_publishCalled() {
        User user = Instancio.create(User.class);

        assertThatNoException().isThrownBy(() -> publisher.publish(user));
    }
}
