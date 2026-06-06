package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.command.CreateUserCommand;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserKafkaPublisherPort;
import com.mycompany.template.core.ports.out.UserNotificationPublisherPort;
import com.mycompany.template.core.ports.out.UserQueuePublisherPort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.UUID;

@Named
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserCachePort userCachePort;
    private final UserKafkaPublisherPort userKafkaPublisherPort;
    private final UserQueuePublisherPort userQueuePublisherPort;
    private final UserNotificationPublisherPort userNotificationPublisherPort;

    public CreateUserUseCaseImpl(
            UserRepositoryPort userRepositoryPort,
            UserCachePort userCachePort,
            UserKafkaPublisherPort userKafkaPublisherPort,
            UserQueuePublisherPort userQueuePublisherPort,
            UserNotificationPublisherPort userNotificationPublisherPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.userCachePort = userCachePort;
        this.userKafkaPublisherPort = userKafkaPublisherPort;
        this.userQueuePublisherPort = userQueuePublisherPort;
        this.userNotificationPublisherPort = userNotificationPublisherPort;
    }

    @Override
    public User execute(CreateUserCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        User user = new User(UUID.randomUUID(), command.name(), command.email(), LocalDateTime.now());
        User saved = userRepositoryPort.save(user);
        userCachePort.put(saved);
        userKafkaPublisherPort.publish(saved);
        userQueuePublisherPort.publish(saved);
        userNotificationPublisherPort.publish(saved);
        return saved;
    }
}
