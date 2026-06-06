package com.mycompany.template.infra.api.dto;

import jakarta.validation.constraints.Email;

public record PatchUserRequest(
        String name,

        @Email(message = "Email must be a valid address")
        String email
) {
}
