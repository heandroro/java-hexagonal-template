package com.mycompany.template.infra.client.feign.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExternalUserResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
