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
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserProtoMapper {

    @Mapping(target = "createdAt", expression = "java(user.createdAt().toString())")
    UserProtoResponse toProtoResponse(User user);

    @Mapping(target = "usersList", source = "content")
    ListUsersProtoResponse toProtoListResponse(UserPage page);

    CreateUserCommand toCommand(CreateUserRequest request);

    UpdateUserCommand toCommand(UpdateUserRequest request);

    default PatchUserCommand toPatchCommand(PatchUserRequest request) {
        return new PatchUserCommand(
                request.hasName() ? request.getName() : null,
                request.hasEmail() ? request.getEmail() : null
        );
    }
}
