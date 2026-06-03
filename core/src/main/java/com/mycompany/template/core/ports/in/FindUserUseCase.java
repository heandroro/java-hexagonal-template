package com.mycompany.template.core.ports.in;

import com.mycompany.template.core.domain.User;

import java.util.UUID;

public interface FindUserUseCase {

    User execute(UUID id);
}
