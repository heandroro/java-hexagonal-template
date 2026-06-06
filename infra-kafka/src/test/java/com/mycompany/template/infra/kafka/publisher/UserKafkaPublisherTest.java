package com.mycompany.template.infra.kafka.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.kafka.avro.UserEvent;
import com.mycompany.template.infra.kafka.mapper.UserKafkaMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserKafkaPublisherTest {

    @Mock
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Mock
    private UserKafkaMapper userKafkaMapper;

    @InjectMocks
    private UserKafkaPublisher userKafkaPublisher;

    @Test
    void should_sendAvroEvent_when_publishCalled() {
        ReflectionTestUtils.setField(userKafkaPublisher, "topic", "user.created");
        var user = Instancio.create(User.class);
        var event = UserEvent.newBuilder()
                .setId(user.id().toString())
                .setName(user.name())
                .setEmail(user.email())
                .setCreatedAt(user.createdAt().toString())
                .build();
        given(userKafkaMapper.toEvent(user)).willReturn(event);

        userKafkaPublisher.publish(user);

        then(kafkaTemplate).should().send(eq("user.created"), eq(user.id().toString()), eq(event));
    }
}
