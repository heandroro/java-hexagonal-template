package com.mycompany.template.infra.postgres.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import com.mycompany.template.infra.postgres.mapper.UserPostgresMapper;
import com.mycompany.template.infra.postgres.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPostgresMapper userPostgresMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserPostgresMapper userPostgresMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userPostgresMapper = userPostgresMapper;
    }

    @Override
    public User save(User user) {
        return userPostgresMapper.toDomain(
                userJpaRepository.save(userPostgresMapper.toEntity(user))
        );
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(userPostgresMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userJpaRepository.findAll(pageable)
                .stream()
                .map(userPostgresMapper::toDomain)
                .toList();
    }

    @Override
    public long countAll() {
        return userJpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }
}
