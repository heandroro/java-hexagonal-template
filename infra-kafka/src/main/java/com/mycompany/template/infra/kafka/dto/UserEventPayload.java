package com.mycompany.template.infra.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserEventPayload(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
