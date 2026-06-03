package com.mycompany.template.infra.dynamodb.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.dynamodb.entity.UserDynamoDbEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserDynamoDbMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
    UserDynamoDbEntity toEntity(User user);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "stringToLocalDateTime")
    User toDomain(UserDynamoDbEntity entity);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : null;
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateTime) {
        return dateTime != null ? LocalDateTime.parse(dateTime) : null;
    }
}
