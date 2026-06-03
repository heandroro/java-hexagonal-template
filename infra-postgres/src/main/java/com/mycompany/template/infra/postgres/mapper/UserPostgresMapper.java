package com.mycompany.template.infra.postgres.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.postgres.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPostgresMapper {

    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);
}
