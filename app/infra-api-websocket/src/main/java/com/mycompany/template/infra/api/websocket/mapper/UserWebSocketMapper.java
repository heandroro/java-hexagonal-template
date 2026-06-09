package com.mycompany.template.infra.api.websocket.mapper;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.api.websocket.dto.UserWebSocketEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserWebSocketMapper {

    UserWebSocketEvent toWebSocketEvent(User user);

    List<UserWebSocketEvent> toWebSocketEventList(List<User> users);
}
