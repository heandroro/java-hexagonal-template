package com.mycompany.template.core.ports.in;

import com.mycompany.template.core.domain.UserPage;

public interface ListUsersUseCase {

    UserPage execute(int page, int size);
}
