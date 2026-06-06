package com.mycompany.template.core.ports.out;

import com.mycompany.template.core.domain.User;

public interface UserQueuePublisherPort {
    void publish(User user);
}
