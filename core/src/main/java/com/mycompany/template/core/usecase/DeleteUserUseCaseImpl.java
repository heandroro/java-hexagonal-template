package com.mycompany.template.core.usecase;

import com.mycompany.template.core.exception.UserNotFoundException;
import com.mycompany.template.core.ports.in.DeleteUserUseCase;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserCachePort userCachePort;

    public DeleteUserUseCaseImpl(UserRepositoryPort userRepositoryPort, UserCachePort userCachePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.userCachePort = userCachePort;
    }

    @Override
    public void execute(UUID id) {
        if (userRepositoryPort.findById(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userRepositoryPort.deleteById(id);
        userCachePort.evict(id);
    }
}
