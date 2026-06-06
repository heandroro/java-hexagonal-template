package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FindUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;

    @InjectMocks
    private FindUserUseCaseImpl findUserUseCase;

    @Nested
    class WhenCacheHit {

        @Test
        void should_returnUser_when_cacheHit() {
            var user = Instancio.create(User.class);
            given(userCachePort.get(user.id())).willReturn(Optional.of(user));

            var result = findUserUseCase.execute(user.id());

            assertThat(result).isEqualTo(user);
            then(userRepositoryPort).shouldHaveNoInteractions();
        }
    }

    @Nested
    class WhenCacheMiss {

        @Test
        void should_fallbackToRepository_when_cacheMiss() {
            var user = Instancio.create(User.class);
            given(userCachePort.get(user.id())).willReturn(Optional.empty());
            given(userRepositoryPort.findById(user.id())).willReturn(Optional.of(user));

            var result = findUserUseCase.execute(user.id());

            assertThat(result).isEqualTo(user);
        }

        @Test
        void should_throwIllegalArgumentException_when_notFoundInCacheOrRepository() {
            var id = UUID.randomUUID();
            given(userCachePort.get(id)).willReturn(Optional.empty());
            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> findUserUseCase.execute(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(id.toString());
        }
    }
}
