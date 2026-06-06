package com.mycompany.template.core.ports.out;

import com.mycompany.template.core.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);

    boolean existsByEmail(String email);

    List<User> findAll(int page, int size);

    long countAll();

    void deleteById(UUID id);
}
