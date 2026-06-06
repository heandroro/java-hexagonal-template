package com.mycompany.template.infra.api.controller;

import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.infra.api.dto.UserWebSocketEvent;
import com.mycompany.template.infra.api.mapper.UserApiMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class UserWebSocketController {

    private final FindUserUseCase findUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UserApiMapper userApiMapper;

    UserWebSocketController(FindUserUseCase findUserUseCase,
                            ListUsersUseCase listUsersUseCase,
                            UserApiMapper userApiMapper) {
        this.findUserUseCase = findUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.userApiMapper = userApiMapper;
    }

    @MessageMapping("/users/{id}")
    @SendTo("/topic/users/{id}")
    public UserWebSocketEvent findUser(@DestinationVariable UUID id) {
        return userApiMapper.toWebSocketEvent(findUserUseCase.execute(id));
    }

    @MessageMapping("/users")
    @SendTo("/topic/users")
    public List<UserWebSocketEvent> listUsers() {
        return userApiMapper.toWebSocketEventList(
                listUsersUseCase.execute(0, 100).content()
        );
    }
}
