package com.mycompany.template.core.ports.out;

import com.mycompany.template.core.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserCachePort {

    void put(User user);

    Optional<User> get(UUID id);

    void evict(UUID id);
}
