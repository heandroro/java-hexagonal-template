package com.mycompany.template.infra.api.grpc.mapper;

import com.mycompany.template.core.command.CreateUserCommand;
import com.mycompany.template.core.command.PatchUserCommand;
import com.mycompany.template.core.command.UpdateUserCommand;
import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.domain.UserPage;
import com.mycompany.template.infra.api.grpc.proto.CreateUserRequest;
import com.mycompany.template.infra.api.grpc.proto.ListUsersProtoResponse;
import com.mycompany.template.infra.api.grpc.proto.PatchUserRequest;
import com.mycompany.template.infra.api.grpc.proto.UpdateUserRequest;
import com.mycompany.template.infra.api.grpc.proto.UserProtoResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProtoMapperTest {

    private final UserProtoMapper mapper = new UserProtoMapperImpl();

    @Test
    void toProtoResponse_shouldMapAllFields() {
        var user = Instancio.create(User.class);

        UserProtoResponse response = mapper.toProtoResponse(user);

        assertThat(response.getId()).isEqualTo(user.id().toString());
        assertThat(response.getName()).isEqualTo(user.name());
        assertThat(response.getEmail()).isEqualTo(user.email());
        assertThat(response.getCreatedAt()).isEqualTo(user.createdAt().toString());
    }

    @Test
    void toProtoListResponse_shouldMapPageMetadataAndUsers() {
        var users = Instancio.createList(User.class);
        var page = new UserPage(users, users.size(), 1, 0, users.size());

        ListUsersProtoResponse response = mapper.toProtoListResponse(page);

        assertThat(response.getTotalElements()).isEqualTo(users.size());
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(users.size());
        assertThat(response.getUsersList()).hasSize(users.size());
    }

    @Test
    void toCommand_createUser_shouldMapNameAndEmail() {
        var request = CreateUserRequest.newBuilder().setName("Alice").setEmail("alice@example.com").build();

        CreateUserCommand command = mapper.toCommand(request);

        assertThat(command.name()).isEqualTo("Alice");
        assertThat(command.email()).isEqualTo("alice@example.com");
    }

    @Test
    void toCommand_updateUser_shouldMapNameAndEmail() {
        var request = UpdateUserRequest.newBuilder()
                .setId("some-id").setName("Bob").setEmail("bob@example.com").build();

        UpdateUserCommand command = mapper.toCommand(request);

        assertThat(command.name()).isEqualTo("Bob");
        assertThat(command.email()).isEqualTo("bob@example.com");
    }

    @Test
    void toCommand_patch_shouldMapPresentFields() {
        var request = PatchUserRequest.newBuilder().setId("some-id").setName("Patched").build();

        PatchUserCommand command = mapper.toCommand(request);

        assertThat(command.name()).isEqualTo("Patched");
        assertThat(command.email()).isNull();
    }

    @Test
    void toCommand_patch_shouldSetNullForAbsentFields() {
        var request = PatchUserRequest.newBuilder().setId("some-id").build();

        PatchUserCommand command = mapper.toCommand(request);

        assertThat(command.name()).isNull();
        assertThat(command.email()).isNull();
    }

    @Test
    void toCommand_patch_shouldMapPresentEmailWithAbsentName() {
        var request = PatchUserRequest.newBuilder().setId("some-id").setEmail("updated@example.com").build();

        PatchUserCommand command = mapper.toCommand(request);

        assertThat(command.name()).isNull();
        assertThat(command.email()).isEqualTo("updated@example.com");
    }
}
