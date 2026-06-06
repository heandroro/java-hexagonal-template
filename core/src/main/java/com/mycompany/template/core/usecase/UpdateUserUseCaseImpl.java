package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.exception.UserNotFoundException;
import com.mycompany.template.core.ports.in.UpdateUserCommand;
import com.mycompany.template.core.ports.in.UpdateUserUseCase;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserCachePort userCachePort;

    public UpdateUserUseCaseImpl(UserRepositoryPort userRepositoryPort, UserCachePort userCachePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.userCachePort = userCachePort;
    }

    @Override
    public User execute(UUID id, UpdateUserCommand command) {
        var existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!existing.email().equals(command.email()) && userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        var updated = new User(existing.id(), command.name(), command.email(), existing.createdAt());
        var saved = userRepositoryPort.save(updated);
        userCachePort.evict(id);
        userCachePort.put(saved);
        return saved;
    }
}
