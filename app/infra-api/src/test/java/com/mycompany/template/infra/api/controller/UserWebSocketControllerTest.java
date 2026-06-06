package com.mycompany.template.infra.api.controller;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.domain.UserPage;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.infra.api.dto.UserWebSocketEvent;
import com.mycompany.template.infra.api.mapper.UserApiMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserWebSocketControllerTest {

    @Mock
    private FindUserUseCase findUserUseCase;
    @Mock
    private ListUsersUseCase listUsersUseCase;
    @Mock
    private UserApiMapper userApiMapper;
    @InjectMocks
    private UserWebSocketController controller;

    @Test
    void findUser_shouldReturnWebSocketEvent_whenUserExists() {
        UUID id = UUID.randomUUID();
        User user = Instancio.create(User.class);
        UserWebSocketEvent event = Instancio.create(UserWebSocketEvent.class);
        given(findUserUseCase.execute(id)).willReturn(user);
        given(userApiMapper.toWebSocketEvent(user)).willReturn(event);

        UserWebSocketEvent result = controller.findUser(id);

        assertThat(result).isEqualTo(event);
        then(findUserUseCase).should().execute(id);
        then(userApiMapper).should().toWebSocketEvent(user);
    }

    @Test
    void listUsers_shouldReturnWebSocketEventList_whenUsersExist() {
        List<User> users = Instancio.createList(User.class);
        UserPage page = new UserPage(users, users.size(), 1, 0, 100);
        List<UserWebSocketEvent> events = Instancio.createList(UserWebSocketEvent.class);
        given(listUsersUseCase.execute(0, 100)).willReturn(page);
        given(userApiMapper.toWebSocketEventList(users)).willReturn(events);

        List<UserWebSocketEvent> result = controller.listUsers();

        assertThat(result).isEqualTo(events);
        then(listUsersUseCase).should().execute(0, 100);
        then(userApiMapper).should().toWebSocketEventList(users);
    }
}
