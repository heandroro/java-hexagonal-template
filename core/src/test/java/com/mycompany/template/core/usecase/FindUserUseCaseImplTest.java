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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;

    @InjectMocks
    private FindUserUseCaseImpl findUserUseCase;

    @Test
    void execute_shouldReturnUser_whenCacheHit() {
        var user = Instancio.create(User.class);
        when(userCachePort.get(user.id())).thenReturn(Optional.of(user));

        var result = findUserUseCase.execute(user.id());

        assertThat(result).isEqualTo(user);
        verifyNoInteractions(userRepositoryPort);
    }

    @Test
    void execute_shouldFallbackToRepository_whenCacheMiss() {
        var user = Instancio.create(User.class);
        when(userCachePort.get(user.id())).thenReturn(Optional.empty());
        when(userRepositoryPort.findById(user.id())).thenReturn(Optional.of(user));

        var result = findUserUseCase.execute(user.id());

        assertThat(result).isEqualTo(user);
    }

    @Test
    void execute_shouldThrow_whenNotFoundInCacheOrRepository() {
        var id = UUID.randomUUID();
        when(userCachePort.get(id)).thenReturn(Optional.empty());
        when(userRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findUserUseCase.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(id.toString());
    }
}
