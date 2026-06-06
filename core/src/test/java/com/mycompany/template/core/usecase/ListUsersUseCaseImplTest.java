package com.mycompany.template.core.usecase;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ListUsersUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private ListUsersUseCaseImpl listUsersUseCase;

    @Nested
    class WhenUsersExist {

        @Test
        void should_returnUserPage_when_usersFound() {
            var users = Instancio.ofList(User.class).size(3).create();
            given(userRepositoryPort.findAll(0, 10)).willReturn(users);
            given(userRepositoryPort.countAll()).willReturn(3L);

            var result = listUsersUseCase.execute(0, 10);

            assertThat(result.content()).hasSize(3);
            assertThat(result.totalElements()).isEqualTo(3L);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.pageNumber()).isZero();
            assertThat(result.pageSize()).isEqualTo(10);
        }

        @Test
        void should_calculateTotalPages_correctly_when_multiplePages() {
            given(userRepositoryPort.findAll(0, 5)).willReturn(Instancio.ofList(User.class).size(5).create());
            given(userRepositoryPort.countAll()).willReturn(13L);

            var result = listUsersUseCase.execute(0, 5);

            assertThat(result.totalPages()).isEqualTo(3);
        }
    }

    @Nested
    class WhenNoUsersExist {

        @Test
        void should_returnEmptyPage_when_noUsersFound() {
            given(userRepositoryPort.findAll(0, 10)).willReturn(List.of());
            given(userRepositoryPort.countAll()).willReturn(0L);

            var result = listUsersUseCase.execute(0, 10);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
            assertThat(result.totalPages()).isZero();
        }
    }
}
