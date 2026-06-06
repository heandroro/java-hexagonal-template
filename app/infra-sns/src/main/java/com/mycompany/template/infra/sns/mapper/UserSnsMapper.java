package com.mycompany.template.infra.sns.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.sns.dto.UserSnsNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSnsMapper {

    @Mapping(target = "id",        expression = "java(user.id().toString())")
    @Mapping(target = "createdAt", expression = "java(user.createdAt().toString())")
    UserSnsNotification toNotification(User user);
}
