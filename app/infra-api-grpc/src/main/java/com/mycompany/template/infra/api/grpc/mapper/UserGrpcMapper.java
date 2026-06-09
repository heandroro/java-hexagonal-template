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
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserGrpcMapper {

    default UserProtoResponse toProtoResponse(User user) {
        return UserProtoResponse.newBuilder()
                .setId(user.id().toString())
                .setName(user.name())
                .setEmail(user.email())
                .setCreatedAt(user.createdAt().toString())
                .build();
    }

    default ListUsersProtoResponse toProtoListResponse(UserPage page) {
        var builder = ListUsersProtoResponse.newBuilder()
                .setTotalElements(page.totalElements())
                .setTotalPages(page.totalPages())
                .setPageNumber(page.pageNumber())
                .setPageSize(page.pageSize());
        page.content().stream().map(this::toProtoResponse).forEach(builder::addUsers);
        return builder.build();
    }

    default CreateUserCommand toCommand(CreateUserRequest request) {
        return new CreateUserCommand(request.getName(), request.getEmail());
    }

    default UpdateUserCommand toCommand(UpdateUserRequest request) {
        return new UpdateUserCommand(request.getName(), request.getEmail());
    }

    default PatchUserCommand toPatchCommand(PatchUserRequest request) {
        return new PatchUserCommand(
                request.hasName() ? request.getName() : null,
                request.hasEmail() ? request.getEmail() : null
        );
    }
}
