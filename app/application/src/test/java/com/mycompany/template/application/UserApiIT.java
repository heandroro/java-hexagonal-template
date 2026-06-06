package com.mycompany.template.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.template.infra.api.dto.CreateUserRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.cloud.compatibility-verifier.enabled=false"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
@Tag("integration")
class UserApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_createUser_and_return201() throws Exception {
        var request = new CreateUserRequest("John Doe", uniqueEmail());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.email").value(request.email()));
    }

    @Test
    void should_return400_when_emailIsInvalid() throws Exception {
        var request = new CreateUserRequest("John", "not-an-email");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return400_when_nameIsBlank() throws Exception {
        var request = new CreateUserRequest("", uniqueEmail());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_findUserById_when_userWasPreviouslyCreated() throws Exception {
        var request = new CreateUserRequest("Jane Doe", uniqueEmail());

        var created = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var id = objectMapper.readTree(created.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value(request.email()));
    }

    private static String uniqueEmail() {
        return UUID.randomUUID() + "@example.com";
    }
}
