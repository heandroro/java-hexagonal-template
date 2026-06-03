package com.mycompany.template.infra.api.controller;

import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.infra.api.dto.CreateUserRequest;
import com.mycompany.template.infra.api.dto.UserResponse;
import com.mycompany.template.infra.api.mapper.UserApiMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final UserApiMapper userApiMapper;

    public UserController(CreateUserUseCase createUserUseCase,
                          FindUserUseCase findUserUseCase,
                          UserApiMapper userApiMapper) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
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
}
