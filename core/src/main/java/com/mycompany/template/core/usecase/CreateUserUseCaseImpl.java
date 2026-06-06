package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.UUID;

@Named
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserCachePort userCachePort;

    public CreateUserUseCaseImpl(UserRepositoryPort userRepositoryPort, UserCachePort userCachePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.userCachePort = userCachePort;
    }

    @Override
    public User execute(String name, String email) {
        if (userRepositoryPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        User user = new User(UUID.randomUUID(), name, email, LocalDateTime.now());
        User saved = userRepositoryPort.save(user);
        userCachePort.put(saved);
        return saved;
    }
}
