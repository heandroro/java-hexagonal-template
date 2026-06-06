package com.mycompany.template.core.ports.in;

import java.util.UUID;

public interface DeleteUserUseCase {

    void execute(UUID id);
}
