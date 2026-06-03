package com.mycompany.template.infra.valkey.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserCachePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserCacheAdapter implements UserCachePort {

    private static final String KEY_PREFIX = "user:";

    private final RedisTemplate<String, User> redisTemplate;
    private final Duration ttl;

    public UserCacheAdapter(RedisTemplate<String, User> redisTemplate,
                            @Value("${app.cache.user.ttl}") Duration ttl) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
    }

    @Override
    public void put(User user) {
        redisTemplate.opsForValue().set(KEY_PREFIX + user.id(), user, ttl);
    }

    @Override
    public Optional<User> get(UUID id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + id));
    }

    @Override
    public void evict(UUID id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }
}
