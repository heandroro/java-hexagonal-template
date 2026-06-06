package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;

    @InjectMocks
    private UpdateUserUseCaseImpl updateUserUseCase;

    @Nested
    class WhenUserExists {

        @Test
        void should_saveAndRefreshCache_when_emailIsUnchanged() {
            var existing = Instancio.create(User.class);
            var saved = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.save(any(User.class))).willReturn(saved);

            var result = updateUserUseCase.execute(existing.id(), "New Name", existing.email());

            assertThat(result).isEqualTo(saved);
            then(userRepositoryPort).should().save(any(User.class));
            then(userCachePort).should().evict(existing.id());
            then(userCachePort).should().put(saved);
        }

        @Test
        void should_saveAndRefreshCache_when_emailChangedAndNotTaken() {
            var existing = Instancio.create(User.class);
            var saved = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.existsByEmail("new@example.com")).willReturn(false);
            given(userRepositoryPort.save(any(User.class))).willReturn(saved);

            var result = updateUserUseCase.execute(existing.id(), existing.name(), "new@example.com");

            assertThat(result).isEqualTo(saved);
        }

        @Test
        void should_throwUserAlreadyExistsException_when_newEmailBelongsToAnotherUser() {
            var existing = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.existsByEmail("taken@example.com")).willReturn(true);

            assertThatThrownBy(() -> updateUserUseCase.execute(existing.id(), existing.name(), "taken@example.com"))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("taken@example.com");
        }
    }

    @Nested
    class WhenUserDoesNotExist {

        @Test
        void should_throwUserNotFoundException_when_idNotFound() {
            var id = UUID.randomUUID();
            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> updateUserUseCase.execute(id, "Name", "email@example.com"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(id.toString());
        }
    }
}
