package com.mycompany.template.infra.postgres.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.postgres.entity.UserEntity;
import com.mycompany.template.infra.postgres.mapper.UserPostgresMapper;
import com.mycompany.template.infra.postgres.repository.UserJpaRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserPostgresMapper userPostgresMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Nested
    class Save {

        @Test
        void should_persistAndReturnMappedDomain() {
            var user = Instancio.create(User.class);
            var entity = Instancio.create(UserEntity.class);
            given(userPostgresMapper.toEntity(user)).willReturn(entity);
            given(userJpaRepository.save(entity)).willReturn(entity);
            given(userPostgresMapper.toDomain(entity)).willReturn(user);

            var result = userRepositoryAdapter.save(user);

            assertThat(result).isEqualTo(user);
            then(userJpaRepository).should().save(entity);
        }
    }

    @Nested
    class FindById {

        @Test
        void should_returnMappedUser_when_entityExists() {
            var user = Instancio.create(User.class);
            var entity = Instancio.create(UserEntity.class);
            given(userJpaRepository.findById(user.id())).willReturn(Optional.of(entity));
            given(userPostgresMapper.toDomain(entity)).willReturn(user);

            assertThat(userRepositoryAdapter.findById(user.id())).contains(user);
        }

        @Test
        void should_returnEmpty_when_entityNotFound() {
            var id = UUID.randomUUID();
            given(userJpaRepository.findById(id)).willReturn(Optional.empty());

            assertThat(userRepositoryAdapter.findById(id)).isEmpty();
        }
    }

    @Nested
    class ExistsByEmail {

        @Test
        void should_delegateToRepository() {
            given(userJpaRepository.existsByEmail("test@example.com")).willReturn(true);

            assertThat(userRepositoryAdapter.existsByEmail("test@example.com")).isTrue();
        }
    }
}
