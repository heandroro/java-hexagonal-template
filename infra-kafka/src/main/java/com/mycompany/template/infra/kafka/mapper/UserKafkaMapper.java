package com.mycompany.template.infra.kafka.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.kafka.dto.UserEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserKafkaMapper {

    User toDomain(UserEventPayload payload);

    UserEventPayload toPayload(User user);
}
