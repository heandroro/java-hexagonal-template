package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.exception.UserAlreadyExistsException;
import com.mycompany.template.core.ports.in.CreateUserCommand;
import com.mycompany.template.core.ports.out.UserCachePort;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;

    @InjectMocks
    private CreateUserUseCaseImpl createUserUseCase;

    @Nested
    class WhenEmailIsNew {

        @Test
        void should_saveAndCacheUser_when_emailDoesNotExist() {
            var savedUser = Instancio.create(User.class);
            given(userRepositoryPort.existsByEmail(savedUser.email())).willReturn(false);
            given(userRepositoryPort.save(any(User.class))).willReturn(savedUser);

            var result = createUserUseCase.execute(new CreateUserCommand(savedUser.name(), savedUser.email()));

            assertThat(result).isEqualTo(savedUser);
            then(userRepositoryPort).should().save(any(User.class));
            then(userCachePort).should().put(savedUser);
        }
    }

    @Nested
    class WhenEmailAlreadyExists {

        @Test
        void should_throwUserAlreadyExistsException_when_emailAlreadyExists() {
            var existingUser = Instancio.create(User.class);
            given(userRepositoryPort.existsByEmail(existingUser.email())).willReturn(true);

            assertThatThrownBy(() -> createUserUseCase.execute(new CreateUserCommand(existingUser.name(), existingUser.email())))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining(existingUser.email());
        }
    }
}
