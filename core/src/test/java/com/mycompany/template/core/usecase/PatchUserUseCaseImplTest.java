package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.exception.UserNotFoundException;
import com.mycompany.template.core.ports.in.PatchUserCommand;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PatchUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;

    @InjectMocks
    private PatchUserUseCaseImpl patchUserUseCase;

    @Nested
    class Execute {

        @Test
        void should_updateOnlyName_when_emailIsNull() {
            var existing = Instancio.create(User.class);
            var saved = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.save(any())).willReturn(saved);

            var result = patchUserUseCase.execute(existing.id(), new PatchUserCommand("New Name", null));

            assertThat(result).isEqualTo(saved);
            then(userRepositoryPort).should(never()).existsByEmail(any());
            then(userCachePort).should().evict(existing.id());
            then(userCachePort).should().put(saved);
        }

        @Test
        void should_updateOnlyEmail_when_nameIsNull() {
            var existing = Instancio.create(User.class);
            var newEmail = "new@example.com";
            var saved = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.existsByEmail(newEmail)).willReturn(false);
            given(userRepositoryPort.save(any())).willReturn(saved);

            var result = patchUserUseCase.execute(existing.id(), new PatchUserCommand(null, newEmail));

            assertThat(result).isEqualTo(saved);
            then(userCachePort).should().evict(existing.id());
            then(userCachePort).should().put(saved);
        }

        @Test
        void should_updateBothFields_when_nameAndEmailProvided() {
            var existing = Instancio.create(User.class);
            var saved = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.existsByEmail("both@example.com")).willReturn(false);
            given(userRepositoryPort.save(any())).willReturn(saved);

            var result = patchUserUseCase.execute(existing.id(), new PatchUserCommand("Both Name", "both@example.com"));

            assertThat(result).isEqualTo(saved);
        }

        @Test
        void should_keepExistingValues_when_bothFieldsAreNull() {
            var existing = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.save(any())).willReturn(existing);

            var result = patchUserUseCase.execute(existing.id(), new PatchUserCommand(null, null));

            assertThat(result).isEqualTo(existing);
            then(userRepositoryPort).should(never()).existsByEmail(any());
        }

        @Test
        void should_notCheckEmailConflict_when_emailUnchanged() {
            var existing = Instancio.create(User.class);
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.save(any())).willReturn(existing);

            patchUserUseCase.execute(existing.id(), new PatchUserCommand("New Name", existing.email()));

            then(userRepositoryPort).should(never()).existsByEmail(any());
        }

        @Test
        void should_throwUserNotFoundException_when_userDoesNotExist() {
            var id = UUID.randomUUID();
            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> patchUserUseCase.execute(id, new PatchUserCommand("Name", null)))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void should_throwUserAlreadyExistsException_when_newEmailAlreadyTaken() {
            var existing = Instancio.create(User.class);
            var takenEmail = "taken@example.com";
            given(userRepositoryPort.findById(existing.id())).willReturn(Optional.of(existing));
            given(userRepositoryPort.existsByEmail(takenEmail)).willReturn(true);

            assertThatThrownBy(() -> patchUserUseCase.execute(existing.id(), new PatchUserCommand(null, takenEmail)))
                    .isInstanceOf(UserAlreadyExistsException.class);
        }
    }
}
