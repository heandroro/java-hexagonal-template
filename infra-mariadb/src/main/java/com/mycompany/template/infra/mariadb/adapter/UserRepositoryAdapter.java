package com.mycompany.template.infra.mariadb.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import com.mycompany.template.infra.mariadb.mapper.UserMariadbMapper;
import com.mycompany.template.infra.mariadb.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMariadbMapper userMariadbMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserMariadbMapper userMariadbMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMariadbMapper = userMariadbMapper;
    }

    @Override
    public User save(User user) {
        return userMariadbMapper.toDomain(
                userJpaRepository.save(userMariadbMapper.toEntity(user))
        );
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(userMariadbMapper::toDomain);
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
                .map(userMariadbMapper::toDomain)
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
