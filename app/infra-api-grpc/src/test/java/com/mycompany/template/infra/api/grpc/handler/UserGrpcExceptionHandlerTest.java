package com.mycompany.template.infra.api.grpc.handler;

import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.exception.UserNotFoundException;
import io.grpc.Status;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserGrpcExceptionHandlerTest {

    private final UserGrpcExceptionHandler handler = new UserGrpcExceptionHandler();

    @Test
    void handleUserNotFound_shouldReturnNotFoundStatus() {
        var ex = new UserNotFoundException(UUID.randomUUID());

        var result = handler.handleUserNotFound(ex);

        assertThat(result.getStatus().getCode()).isEqualTo(Status.NOT_FOUND.getCode());
        assertThat(result.getStatus().getDescription()).isEqualTo(ex.getMessage());
    }

    @Test
    void handleUserAlreadyExists_shouldReturnAlreadyExistsStatus() {
        var ex = new UserAlreadyExistsException("duplicate@example.com");

        var result = handler.handleUserAlreadyExists(ex);

        assertThat(result.getStatus().getCode()).isEqualTo(Status.ALREADY_EXISTS.getCode());
        assertThat(result.getStatus().getDescription()).isEqualTo(ex.getMessage());
    }

    @Test
    void handleIllegalArgument_shouldReturnInvalidArgumentStatus() {
        var ex = new IllegalArgumentException("Invalid UUID string: invalid");

        var result = handler.handleIllegalArgument(ex);

        assertThat(result.getStatus().getCode()).isEqualTo(Status.INVALID_ARGUMENT.getCode());
        assertThat(result.getStatus().getDescription()).isEqualTo(ex.getMessage());
    }
}
