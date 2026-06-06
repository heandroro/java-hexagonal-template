package com.mycompany.template.core.ports.in;

import com.mycompany.template.core.domain.User;

import java.util.UUID;

public interface UpdateUserUseCase {

    User execute(UUID id, UpdateUserCommand command);
}
