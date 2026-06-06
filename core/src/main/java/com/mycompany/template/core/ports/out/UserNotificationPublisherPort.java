package com.mycompany.template.core.ports.out;

import com.mycompany.template.core.domain.User;

public interface UserNotificationPublisherPort {
    void publish(User user);
}
