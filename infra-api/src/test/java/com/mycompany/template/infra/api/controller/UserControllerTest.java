package com.mycompany.template.infra.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.infra.api.dto.CreateUserRequest;
import com.mycompany.template.infra.api.dto.UserResponse;
import com.mycompany.template.infra.api.mapper.UserApiMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
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
            var invalidRequest = new CreateUserRequest("John", "not-an-email");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void should_return400_when_nameIsBlank() throws Exception {
            var invalidRequest = new CreateUserRequest("", "john@example.com");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
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
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.email").value(response.email()));
        }
    }
}
