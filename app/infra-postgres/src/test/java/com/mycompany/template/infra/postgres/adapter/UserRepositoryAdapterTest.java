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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    class FindAll {

        @Test
        void should_returnMappedUsers_when_entitiesExist() {
            var entity = Instancio.create(UserEntity.class);
            var user = Instancio.create(User.class);
            var jpaPage = new PageImpl<>(List.of(entity));
            given(userJpaRepository.findAll(any(Pageable.class))).willReturn(jpaPage);
            given(userPostgresMapper.toDomain(entity)).willReturn(user);

            var result = userRepositoryAdapter.findAll(0, 10);

            assertThat(result).containsExactly(user);
        }
    }

    @Nested
    class CountAll {

        @Test
        void should_delegateCountToRepository() {
            given(userJpaRepository.count()).willReturn(42L);

            assertThat(userRepositoryAdapter.countAll()).isEqualTo(42L);
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

    @Nested
    class DeleteById {

        @Test
        void should_delegateDeletionToRepository() {
            var id = UUID.randomUUID();

            userRepositoryAdapter.deleteById(id);

            then(userJpaRepository).should().deleteById(id);
        }
    }
}
