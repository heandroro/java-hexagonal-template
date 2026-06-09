package com.mycompany.template.infra.api.rest.mapper;

import com.mycompany.template.core.command.CreateUserCommand;
import com.mycompany.template.core.command.PatchUserCommand;
import com.mycompany.template.core.command.UpdateUserCommand;
import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.api.rest.dto.CreateUserRequest;
import com.mycompany.template.infra.api.rest.dto.PatchUserRequest;
import com.mycompany.template.infra.api.rest.dto.UpdateUserRequest;
import com.mycompany.template.infra.api.rest.dto.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserApiMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    CreateUserCommand toCommand(CreateUserRequest request);

    UpdateUserCommand toCommand(UpdateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PatchUserCommand toCommand(PatchUserRequest request);
}
