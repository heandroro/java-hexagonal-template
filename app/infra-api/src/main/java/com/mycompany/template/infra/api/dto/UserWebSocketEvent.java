package com.mycompany.template.infra.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserWebSocketEvent(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
