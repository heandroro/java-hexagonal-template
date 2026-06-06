package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserNotFoundException;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;

    @InjectMocks
    private DeleteUserUseCaseImpl deleteUserUseCase;

    @Nested
    class WhenUserExists {

        @Test
        void should_deleteFromRepositoryAndEvictCache_when_userFound() {
            var user = Instancio.create(User.class);
            given(userRepositoryPort.findById(user.id())).willReturn(Optional.of(user));

            deleteUserUseCase.execute(user.id());

            then(userRepositoryPort).should().deleteById(user.id());
            then(userCachePort).should().evict(user.id());
        }
    }

    @Nested
    class WhenUserDoesNotExist {

        @Test
        void should_throwUserNotFoundException_when_idNotFound() {
            var id = UUID.randomUUID();
            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> deleteUserUseCase.execute(id))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(id.toString());
        }
    }
}
