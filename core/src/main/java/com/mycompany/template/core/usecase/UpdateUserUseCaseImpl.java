package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.exception.UserNotFoundException;
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
    public User execute(UUID id, String name, String email) {
        var existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!existing.email().equals(email) && userRepositoryPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        var updated = new User(existing.id(), name, email, existing.createdAt());
        var saved = userRepositoryPort.save(updated);
        userCachePort.evict(id);
        userCachePort.put(saved);
        return saved;
    }
}
