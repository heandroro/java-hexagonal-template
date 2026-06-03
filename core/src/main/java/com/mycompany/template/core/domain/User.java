package com.mycompany.template.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
