package com.mycompany.template.infra.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.domain.UserPage;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.exception.UserNotFoundException;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.in.DeleteUserUseCase;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.core.ports.in.PatchUserCommand;
import com.mycompany.template.core.ports.in.PatchUserUseCase;
import com.mycompany.template.core.ports.in.UpdateUserUseCase;
import com.mycompany.template.infra.api.dto.CreateUserRequest;
import com.mycompany.template.infra.api.dto.PatchUserRequest;
import com.mycompany.template.infra.api.dto.UpdateUserRequest;
import com.mycompany.template.infra.api.dto.UserResponse;
import com.mycompany.template.infra.api.handler.ApiExceptionHandler;
import com.mycompany.template.infra.api.mapper.UserApiMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ApiExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private FindUserUseCase findUserUseCase;

    @MockitoBean
    private ListUsersUseCase listUsersUseCase;

    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;

    @MockitoBean
    private PatchUserUseCase patchUserUseCase;

    @MockitoBean
    private DeleteUserUseCase deleteUserUseCase;

    @MockitoBean
    private UserApiMapper userApiMapper;

    @Nested
    class Create {

        @Test
        void should_return201_when_requestIsValid() throws Exception {
            var request = new CreateUserRequest("John Doe", "john@example.com");
            var user = Instancio.create(User.class);
            var response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt());
            given(createUserUseCase.execute(request.name(), request.email())).willReturn(user);
            given(userApiMapper.toResponse(user)).willReturn(response);

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.email").value(response.email()));
        }

        @Test
        void should_return400_when_emailIsInvalid() throws Exception {
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CreateUserRequest("John", "not-an-email"))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void should_return400_when_nameIsBlank() throws Exception {
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CreateUserRequest("", "john@example.com"))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void should_return409_when_emailAlreadyExists() throws Exception {
            var request = new CreateUserRequest("John", "dup@example.com");
            given(createUserUseCase.execute(request.name(), request.email()))
                    .willThrow(new UserAlreadyExistsException(request.email()));

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("User Already Exists"));
        }
    }

    @Nested
    class FindById {

        @Test
        void should_return200_when_userFound() throws Exception {
            var user = Instancio.create(User.class);
            var response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt());
            given(findUserUseCase.execute(user.id())).willReturn(user);
            given(userApiMapper.toResponse(user)).willReturn(response);

            mockMvc.perform(get("/api/v1/users/{id}", user.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()));
        }

        @Test
        void should_return404_when_userNotFound() throws Exception {
            var id = UUID.randomUUID();
            given(findUserUseCase.execute(id)).willThrow(new UserNotFoundException(id));

            mockMvc.perform(get("/api/v1/users/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"));
        }
    }

    @Nested
    class ListAll {

        @Test
        void should_return200WithPage_when_usersExist() throws Exception {
            var user = Instancio.create(User.class);
            var response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt());
            var userPage = new UserPage(List.of(user), 1L, 1, 0, 20);
            given(listUsersUseCase.execute(anyInt(), anyInt())).willReturn(userPage);
            given(userApiMapper.toResponseList(List.of(user))).willReturn(List.of(response));

            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(response.id().toString()))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    class Update {

        @Test
        void should_return200_when_updateIsValid() throws Exception {
            var id = UUID.randomUUID();
            var request = new UpdateUserRequest("Jane Doe", "jane@example.com");
            var user = Instancio.create(User.class);
            var response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt());
            given(updateUserUseCase.execute(eq(id), any(), any())).willReturn(user);
            given(userApiMapper.toResponse(user)).willReturn(response);

            mockMvc.perform(put("/api/v1/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()));
        }

        @Test
        void should_return404_when_userNotFound() throws Exception {
            var id = UUID.randomUUID();
            var request = new UpdateUserRequest("Jane", "jane@example.com");
            given(updateUserUseCase.execute(eq(id), any(), any()))
                    .willThrow(new UserNotFoundException(id));

            mockMvc.perform(put("/api/v1/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void should_return409_when_emailAlreadyTaken() throws Exception {
            var id = UUID.randomUUID();
            var request = new UpdateUserRequest("Jane", "taken@example.com");
            given(updateUserUseCase.execute(eq(id), any(), any()))
                    .willThrow(new UserAlreadyExistsException(request.email()));

            mockMvc.perform(put("/api/v1/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        void should_return400_when_requestBodyIsInvalid() throws Exception {
            mockMvc.perform(put("/api/v1/users/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new UpdateUserRequest("", "bad"))))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class Patch {

        @Test
        void should_return200_when_patchIsValid() throws Exception {
            var id = UUID.randomUUID();
            var request = new PatchUserRequest("Patched Name", null);
            var command = new PatchUserCommand(request.name(), request.email());
            var user = Instancio.create(User.class);
            var response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt());
            given(userApiMapper.toCommand(any())).willReturn(command);
            given(patchUserUseCase.execute(eq(id), eq(command))).willReturn(user);
            given(userApiMapper.toResponse(user)).willReturn(response);

            mockMvc.perform(patch("/api/v1/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()));
        }

        @Test
        void should_return400_when_emailIsInvalid() throws Exception {
            mockMvc.perform(patch("/api/v1/users/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"not-an-email\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void should_return404_when_userNotFound() throws Exception {
            var id = UUID.randomUUID();
            var command = new PatchUserCommand(null, null);
            given(userApiMapper.toCommand(any())).willReturn(command);
            given(patchUserUseCase.execute(eq(id), eq(command)))
                    .willThrow(new UserNotFoundException(id));

            mockMvc.perform(patch("/api/v1/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"));
        }

        @Test
        void should_return409_when_emailAlreadyTaken() throws Exception {
            var id = UUID.randomUUID();
            var command = new PatchUserCommand(null, "taken@example.com");
            given(userApiMapper.toCommand(any())).willReturn(command);
            given(patchUserUseCase.execute(eq(id), eq(command)))
                    .willThrow(new UserAlreadyExistsException("taken@example.com"));

            mockMvc.perform(patch("/api/v1/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"taken@example.com\"}"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("User Already Exists"));
        }
    }

    @Nested
    class Delete {

        @Test
        void should_return204_when_userDeleted() throws Exception {
            var id = UUID.randomUUID();
            willDoNothing().given(deleteUserUseCase).execute(id);

            mockMvc.perform(delete("/api/v1/users/{id}", id))
                    .andExpect(status().isNoContent());
        }

        @Test
        void should_return404_when_userNotFound() throws Exception {
            var id = UUID.randomUUID();
            willThrow(new UserNotFoundException(id)).given(deleteUserUseCase).execute(id);

            mockMvc.perform(delete("/api/v1/users/{id}", id))
                    .andExpect(status().isNotFound());
        }
    }
}
