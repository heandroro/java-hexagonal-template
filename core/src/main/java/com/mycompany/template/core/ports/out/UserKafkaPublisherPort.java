package com.mycompany.template.core.ports.out;

import com.mycompany.template.core.domain.User;

public interface UserKafkaPublisherPort {
    void publish(User user);
}
