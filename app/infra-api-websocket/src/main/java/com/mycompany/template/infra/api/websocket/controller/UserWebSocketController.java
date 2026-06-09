package com.mycompany.template.infra.api.websocket.controller;

import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.infra.api.websocket.dto.UserWebSocketEvent;
import com.mycompany.template.infra.api.websocket.mapper.UserWebSocketMapper;
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
    private final UserWebSocketMapper userWebSocketMapper;

    UserWebSocketController(FindUserUseCase findUserUseCase,
                            ListUsersUseCase listUsersUseCase,
                            UserWebSocketMapper userWebSocketMapper) {
        this.findUserUseCase = findUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.userWebSocketMapper = userWebSocketMapper;
    }

    @MessageMapping("/users/{id}")
    @SendTo("/topic/users/{id}")
    public UserWebSocketEvent findUser(@DestinationVariable UUID id) {
        return userWebSocketMapper.toWebSocketEvent(findUserUseCase.execute(id));
    }

    @MessageMapping("/users")
    @SendTo("/topic/users")
    public List<UserWebSocketEvent> listUsers() {
        return userWebSocketMapper.toWebSocketEventList(
                listUsersUseCase.execute(0, 100).content()
        );
    }
}
