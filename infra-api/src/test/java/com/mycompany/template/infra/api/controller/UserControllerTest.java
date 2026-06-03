package com.mycompany.template.infra.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.infra.api.dto.CreateUserRequest;
import com.mycompany.template.infra.api.dto.UserResponse;
import com.mycompany.template.infra.api.mapper.UserApiMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
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

    @Test
    void create_shouldReturn201_whenRequestIsValid() throws Exception {
        var request = new CreateUserRequest("John Doe", "john@example.com");
        var user = Instancio.create(User.class);
        var response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt());

        when(createUserUseCase.execute(request.name(), request.email())).thenReturn(user);
        when(userApiMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.email").value(response.email()));
    }

    @Test
    void create_shouldReturn400_whenEmailIsInvalid() throws Exception {
        var invalidRequest = new CreateUserRequest("John", "not-an-email");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenNameIsBlank() throws Exception {
        var invalidRequest = new CreateUserRequest("", "john@example.com");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_shouldReturn200_whenUserFound() throws Exception {
        var user = Instancio.create(User.class);
        var response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt());

        when(findUserUseCase.execute(user.id())).thenReturn(user);
        when(userApiMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/{id}", user.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.email").value(response.email()));
    }
}
