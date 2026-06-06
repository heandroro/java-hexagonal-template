package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.exception.UserNotFoundException;
import com.mycompany.template.core.ports.in.PatchUserCommand;
import com.mycompany.template.core.ports.in.PatchUserUseCase;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class PatchUserUseCaseImpl implements PatchUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserCachePort userCachePort;

    public PatchUserUseCaseImpl(UserRepositoryPort userRepositoryPort, UserCachePort userCachePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.userCachePort = userCachePort;
    }

    @Override
    public User execute(UUID id, PatchUserCommand command) {
        var existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        var finalName = command.name() != null ? command.name() : existing.name();
        var finalEmail = command.email() != null ? command.email() : existing.email();

        if (command.email() != null && !existing.email().equals(finalEmail)
                && userRepositoryPort.existsByEmail(finalEmail)) {
            throw new UserAlreadyExistsException(finalEmail);
        }

        var updated = new User(existing.id(), finalName, finalEmail, existing.createdAt());
        var saved = userRepositoryPort.save(updated);
        userCachePort.evict(id);
        userCachePort.put(saved);
        return saved;
    }
}
