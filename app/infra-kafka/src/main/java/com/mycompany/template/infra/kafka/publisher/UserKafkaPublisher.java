package com.mycompany.template.infra.kafka.publisher;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserKafkaPublisherPort;
import com.mycompany.template.infra.kafka.avro.UserEvent;
import com.mycompany.template.infra.kafka.mapper.UserKafkaMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserKafkaPublisher implements UserKafkaPublisherPort {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final UserKafkaMapper userKafkaMapper;

    @Value("${app.kafka.topics.user-created}")
    private String topic;

    public UserKafkaPublisher(KafkaTemplate<String, UserEvent> kafkaTemplate, UserKafkaMapper userKafkaMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.userKafkaMapper = userKafkaMapper;
    }

    @Override
    public void publish(User user) {
        UserEvent event = userKafkaMapper.toEvent(user);
        kafkaTemplate.send(topic, user.id().toString(), event);
    }
}
