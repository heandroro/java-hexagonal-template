package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.UserPage;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import jakarta.inject.Named;

@Named
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ListUsersUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserPage execute(int page, int size) {
        var content = userRepositoryPort.findAll(page, size);
        var total = userRepositoryPort.countAll();
        var totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        return new UserPage(content, total, totalPages, page, size);
    }
}
