package com.mycompany.template.core.ports.out;

import com.mycompany.template.core.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);

    boolean existsByEmail(String email);
}
