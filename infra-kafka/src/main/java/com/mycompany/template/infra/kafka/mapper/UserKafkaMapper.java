package com.mycompany.template.infra.kafka.mapper;

import com.mycompany.template.core.command.CreateUserCommand;
import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.kafka.avro.UserEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserKafkaMapper {

    @Mapping(target = "id",        expression = "java(user.id().toString())")
    @Mapping(target = "createdAt", expression = "java(user.createdAt().toString())")
    UserEvent toEvent(User user);

    @Mapping(target = "name",  source = "name")
    @Mapping(target = "email", source = "email")
    CreateUserCommand toCommand(UserEvent event);
}
