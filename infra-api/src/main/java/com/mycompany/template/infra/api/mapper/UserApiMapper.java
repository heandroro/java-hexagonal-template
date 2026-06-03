package com.mycompany.template.infra.api.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.api.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserApiMapper {

    UserResponse toResponse(User user);
}
