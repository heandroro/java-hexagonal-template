package com.mycompany.template.infra.api.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.in.PatchUserCommand;
import com.mycompany.template.infra.api.dto.PatchUserRequest;
import com.mycompany.template.infra.api.dto.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserApiMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PatchUserCommand toCommand(PatchUserRequest request);
}
