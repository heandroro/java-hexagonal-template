package com.mycompany.template.infra.api.grpc.handler;

import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.exception.UserNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class UserGrpcExceptionHandler {

    @GrpcExceptionHandler(UserNotFoundException.class)
    public StatusRuntimeException handleUserNotFound(UserNotFoundException ex) {
        return Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(UserAlreadyExistsException.class)
    public StatusRuntimeException handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return Status.ALREADY_EXISTS.withDescription(ex.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusRuntimeException handleIllegalArgument(IllegalArgumentException ex) {
        return Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException();
    }
}
