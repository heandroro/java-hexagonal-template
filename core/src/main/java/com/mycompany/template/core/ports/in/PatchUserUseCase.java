package com.mycompany.template.core.ports.in;

import com.mycompany.template.core.domain.User;

import java.util.UUID;

public interface PatchUserUseCase {

    User execute(UUID id, PatchUserCommand command);
}
