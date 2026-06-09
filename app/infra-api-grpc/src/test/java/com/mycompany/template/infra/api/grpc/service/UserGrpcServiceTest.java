package com.mycompany.template.infra.api.grpc.service;

import com.google.protobuf.Empty;
import com.mycompany.template.core.command.CreateUserCommand;
import com.mycompany.template.core.command.PatchUserCommand;
import com.mycompany.template.core.command.UpdateUserCommand;
import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.domain.UserPage;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.in.DeleteUserUseCase;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.core.ports.in.PatchUserUseCase;
import com.mycompany.template.core.ports.in.UpdateUserUseCase;
import com.mycompany.template.infra.api.grpc.mapper.UserProtoMapper;
import com.mycompany.template.infra.api.grpc.proto.CreateUserRequest;
import com.mycompany.template.infra.api.grpc.proto.ListUsersProtoResponse;
import com.mycompany.template.infra.api.grpc.proto.ListUsersRequest;
import com.mycompany.template.infra.api.grpc.proto.PatchUserRequest;
import com.mycompany.template.infra.api.grpc.proto.UpdateUserRequest;
import com.mycompany.template.infra.api.grpc.proto.UserByIdRequest;
import com.mycompany.template.infra.api.grpc.proto.UserProtoResponse;
import io.grpc.stub.StreamObserver;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserGrpcServiceTest {

    @Mock
    private CreateUserUseCase createUserUseCase;
    @Mock
    private FindUserUseCase findUserUseCase;
    @Mock
    private ListUsersUseCase listUsersUseCase;
    @Mock
    private UpdateUserUseCase updateUserUseCase;
    @Mock
    private PatchUserUseCase patchUserUseCase;
    @Mock
    private DeleteUserUseCase deleteUserUseCase;
    @Mock
    private UserProtoMapper userProtoMapper;

    @InjectMocks
    private UserGrpcService service;

    @Test
    void createUser_shouldReturnProtoResponse_whenRequestIsValid() {
        var request = CreateUserRequest.newBuilder().setName("John").setEmail("john@example.com").build();
        var command = new CreateUserCommand("John", "john@example.com");
        var user = Instancio.create(User.class);
        var protoResp = UserProtoResponse.newBuilder().setId("id").setName("John").build();
        @SuppressWarnings("unchecked")
        var observer = mock(StreamObserver.class);

        given(userProtoMapper.toCommand(request)).willReturn(command);
        given(createUserUseCase.execute(command)).willReturn(user);
        given(userProtoMapper.toProtoResponse(user)).willReturn(protoResp);

        service.createUser(request, observer);

        then(observer).should().onNext(protoResp);
        then(observer).should().onCompleted();
    }

    @Test
    void findUser_shouldReturnProtoResponse_whenUserExists() {
        var id = UUID.randomUUID();
        var request = UserByIdRequest.newBuilder().setId(id.toString()).build();
        var user = Instancio.create(User.class);
        var protoResp = UserProtoResponse.newBuilder().setId(id.toString()).build();
        @SuppressWarnings("unchecked")
        var observer = mock(StreamObserver.class);

        given(findUserUseCase.execute(id)).willReturn(user);
        given(userProtoMapper.toProtoResponse(user)).willReturn(protoResp);

        service.findUser(request, observer);

        then(observer).should().onNext(protoResp);
        then(observer).should().onCompleted();
    }

    @Test
    void listUsers_shouldReturnProtoListResponse_whenUsersExist() {
        var request = ListUsersRequest.newBuilder().setPage(0).setSize(10).build();
        var users = Instancio.createList(User.class);
        var page = new UserPage(users, users.size(), 1, 0, 10);
        var protoListResp = ListUsersProtoResponse.newBuilder().setTotalElements(users.size()).build();
        @SuppressWarnings("unchecked")
        var observer = mock(StreamObserver.class);

        given(listUsersUseCase.execute(0, 10)).willReturn(page);
        given(userProtoMapper.toProtoListResponse(page)).willReturn(protoListResp);

        service.listUsers(request, observer);

        then(observer).should().onNext(protoListResp);
        then(observer).should().onCompleted();
    }

    @Test
    void updateUser_shouldReturnProtoResponse_whenRequestIsValid() {
        var id = UUID.randomUUID();
        var request = UpdateUserRequest.newBuilder()
                .setId(id.toString()).setName("Jane").setEmail("jane@example.com").build();
        var command = new UpdateUserCommand("Jane", "jane@example.com");
        var user = Instancio.create(User.class);
        var protoResp = UserProtoResponse.newBuilder().setId(id.toString()).setName("Jane").build();
        @SuppressWarnings("unchecked")
        var observer = mock(StreamObserver.class);

        given(userProtoMapper.toCommand(request)).willReturn(command);
        given(updateUserUseCase.execute(id, command)).willReturn(user);
        given(userProtoMapper.toProtoResponse(user)).willReturn(protoResp);

        service.updateUser(request, observer);

        then(observer).should().onNext(protoResp);
        then(observer).should().onCompleted();
    }

    @Test
    void patchUser_shouldReturnProtoResponse_whenRequestIsValid() {
        var id = UUID.randomUUID();
        var request = PatchUserRequest.newBuilder().setId(id.toString()).setName("Patched").build();
        var command = new PatchUserCommand("Patched", null);
        var user = Instancio.create(User.class);
        var protoResp = UserProtoResponse.newBuilder().setId(id.toString()).setName("Patched").build();
        @SuppressWarnings("unchecked")
        var observer = mock(StreamObserver.class);

        given(userProtoMapper.toCommand(request)).willReturn(command);
        given(patchUserUseCase.execute(id, command)).willReturn(user);
        given(userProtoMapper.toProtoResponse(user)).willReturn(protoResp);

        service.patchUser(request, observer);

        then(observer).should().onNext(protoResp);
        then(observer).should().onCompleted();
    }

    @Test
    void deleteUser_shouldCompleteObserver_whenUserExists() {
        var id = UUID.randomUUID();
        var request = UserByIdRequest.newBuilder().setId(id.toString()).build();
        @SuppressWarnings("unchecked")
        var observer = mock(StreamObserver.class);

        willDoNothing().given(deleteUserUseCase).execute(id);

        service.deleteUser(request, observer);

        then(observer).should().onNext(Empty.getDefaultInstance());
        then(observer).should().onCompleted();
    }
}
