package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;

    @InjectMocks
    private CreateUserUseCaseImpl createUserUseCase;

    @Test
    void execute_shouldSaveAndCacheUser_whenEmailDoesNotExist() {
        var name = "John Doe";
        var email = "john@example.com";
        var savedUser = Instancio.create(User.class);

        when(userRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        var result = createUserUseCase.execute(name, email);

        assertThat(result).isEqualTo(savedUser);
        verify(userRepositoryPort).save(any(User.class));
        verify(userCachePort).put(savedUser);
    }

    @Test
    void execute_shouldThrowIllegalArgumentException_whenEmailAlreadyExists() {
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> createUserUseCase.execute("John", "john@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("john@example.com");
    }
}
