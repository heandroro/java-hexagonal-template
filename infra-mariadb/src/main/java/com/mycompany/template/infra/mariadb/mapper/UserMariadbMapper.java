package com.mycompany.template.infra.mariadb.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.mariadb.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMariadbMapper {

    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);
}
