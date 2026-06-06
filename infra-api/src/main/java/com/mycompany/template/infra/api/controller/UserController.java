package com.mycompany.template.infra.api.controller;

import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.in.DeleteUserUseCase;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.core.ports.in.PatchUserUseCase;
import com.mycompany.template.core.ports.in.UpdateUserUseCase;
import com.mycompany.template.infra.api.dto.CreateUserRequest;
import com.mycompany.template.infra.api.dto.PatchUserRequest;
import com.mycompany.template.infra.api.dto.UpdateUserRequest;
import com.mycompany.template.infra.api.dto.UserResponse;
import com.mycompany.template.infra.api.mapper.UserApiMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final PatchUserUseCase patchUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserApiMapper userApiMapper;

    public UserController(CreateUserUseCase createUserUseCase,
                          FindUserUseCase findUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          PatchUserUseCase patchUserUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          UserApiMapper userApiMapper) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.patchUserUseCase = patchUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.userApiMapper = userApiMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userApiMapper.toResponse(
                createUserUseCase.execute(request.name(), request.email())
        );
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return userApiMapper.toResponse(findUserUseCase.execute(id));
    }

    @GetMapping
    public Page<UserResponse> listAll(@PageableDefault(size = 20) Pageable pageable) {
        var userPage = listUsersUseCase.execute(pageable.getPageNumber(), pageable.getPageSize());
        return new PageImpl<>(
                userApiMapper.toResponseList(userPage.content()),
                pageable,
                userPage.totalElements()
        );
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UpdateUserRequest request) {
        return userApiMapper.toResponse(
                updateUserUseCase.execute(id, request.name(), request.email())
        );
    }

    @PatchMapping("/{id}")
    public UserResponse patch(@PathVariable UUID id,
                              @Valid @RequestBody PatchUserRequest request) {
        return userApiMapper.toResponse(
                patchUserUseCase.execute(id, userApiMapper.toCommand(request))
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteUserUseCase.execute(id);
    }
}
