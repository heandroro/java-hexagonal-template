package com.mycompany.template.core.ports.in;

import com.mycompany.template.core.domain.User;

public interface CreateUserUseCase {

    User execute(CreateUserCommand command);
}
