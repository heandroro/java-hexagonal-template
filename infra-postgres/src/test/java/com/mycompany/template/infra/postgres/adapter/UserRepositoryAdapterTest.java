package com.mycompany.template.infra.postgres.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.postgres.entity.UserEntity;
import com.mycompany.template.infra.postgres.mapper.UserPostgresMapper;
import com.mycompany.template.infra.postgres.repository.UserJpaRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserPostgresMapper userPostgresMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void save_shouldPersistAndReturnMappedDomain() {
        var user = Instancio.create(User.class);
        var entity = Instancio.create(UserEntity.class);

        when(userPostgresMapper.toEntity(user)).thenReturn(entity);
        when(userJpaRepository.save(entity)).thenReturn(entity);
        when(userPostgresMapper.toDomain(entity)).thenReturn(user);

        var result = userRepositoryAdapter.save(user);

        assertThat(result).isEqualTo(user);
        verify(userJpaRepository).save(entity);
    }

    @Test
    void findById_shouldReturnMappedUser_whenEntityExists() {
        var user = Instancio.create(User.class);
        var entity = Instancio.create(UserEntity.class);

        when(userJpaRepository.findById(user.id())).thenReturn(Optional.of(entity));
        when(userPostgresMapper.toDomain(entity)).thenReturn(user);

        var result = userRepositoryAdapter.findById(user.id());

        assertThat(result).contains(user);
    }

    @Test
    void findById_shouldReturnEmpty_whenEntityNotFound() {
        var id = UUID.randomUUID();
        when(userJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(userRepositoryAdapter.findById(id)).isEmpty();
    }

    @Test
    void existsByEmail_shouldDelegateToRepository() {
        when(userJpaRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThat(userRepositoryAdapter.existsByEmail("test@example.com")).isTrue();
    }
}
