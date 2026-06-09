package com.mycompany.template.infra.api.grpc.service;

import com.google.protobuf.Empty;
import com.mycompany.template.core.ports.in.CreateUserUseCase;
import com.mycompany.template.core.ports.in.DeleteUserUseCase;
import com.mycompany.template.core.ports.in.FindUserUseCase;
import com.mycompany.template.core.ports.in.ListUsersUseCase;
import com.mycompany.template.core.ports.in.PatchUserUseCase;
import com.mycompany.template.core.ports.in.UpdateUserUseCase;
import com.mycompany.template.infra.api.grpc.mapper.UserGrpcMapper;
import com.mycompany.template.infra.api.grpc.proto.CreateUserRequest;
import com.mycompany.template.infra.api.grpc.proto.ListUsersRequest;
import com.mycompany.template.infra.api.grpc.proto.ListUsersProtoResponse;
import com.mycompany.template.infra.api.grpc.proto.PatchUserRequest;
import com.mycompany.template.infra.api.grpc.proto.UpdateUserRequest;
import com.mycompany.template.infra.api.grpc.proto.UserByIdRequest;
import com.mycompany.template.infra.api.grpc.proto.UserProtoResponse;
import com.mycompany.template.infra.api.grpc.proto.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final PatchUserUseCase patchUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserGrpcMapper userGrpcMapper;

    UserGrpcService(CreateUserUseCase createUserUseCase,
                    FindUserUseCase findUserUseCase,
                    ListUsersUseCase listUsersUseCase,
                    UpdateUserUseCase updateUserUseCase,
                    PatchUserUseCase patchUserUseCase,
                    DeleteUserUseCase deleteUserUseCase,
                    UserGrpcMapper userGrpcMapper) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.patchUserUseCase = patchUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.userGrpcMapper = userGrpcMapper;
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserProtoResponse> responseObserver) {
        var user = createUserUseCase.execute(userGrpcMapper.toCommand(request));
        responseObserver.onNext(userGrpcMapper.toProtoResponse(user));
        responseObserver.onCompleted();
    }

    @Override
    public void findUser(UserByIdRequest request, StreamObserver<UserProtoResponse> responseObserver) {
        var user = findUserUseCase.execute(UUID.fromString(request.getId()));
        responseObserver.onNext(userGrpcMapper.toProtoResponse(user));
        responseObserver.onCompleted();
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<ListUsersProtoResponse> responseObserver) {
        var page = listUsersUseCase.execute(request.getPage(), request.getSize());
        responseObserver.onNext(userGrpcMapper.toProtoListResponse(page));
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserProtoResponse> responseObserver) {
        var user = updateUserUseCase.execute(UUID.fromString(request.getId()), userGrpcMapper.toCommand(request));
        responseObserver.onNext(userGrpcMapper.toProtoResponse(user));
        responseObserver.onCompleted();
    }

    @Override
    public void patchUser(PatchUserRequest request, StreamObserver<UserProtoResponse> responseObserver) {
        var user = patchUserUseCase.execute(UUID.fromString(request.getId()), userGrpcMapper.toPatchCommand(request));
        responseObserver.onNext(userGrpcMapper.toProtoResponse(user));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(UserByIdRequest request, StreamObserver<Empty> responseObserver) {
        deleteUserUseCase.execute(UUID.fromString(request.getId()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
