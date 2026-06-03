package com.mycompany.template.infra.valkey.adapter;

import com.mycompany.template.core.domain.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCacheAdapterTest {

    @Mock
    private RedisTemplate<String, User> redisTemplate;

    @Mock
    private ValueOperations<String, User> valueOperations;

    private UserCacheAdapter userCacheAdapter;

    private static final Duration TTL = Duration.ofMinutes(30);

    @BeforeEach
    void setUp() {
        userCacheAdapter = new UserCacheAdapter(redisTemplate, TTL);
    }

    @Test
    void put_shouldStoreUserWithConfiguredTtl() {
        var user = Instancio.create(User.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        userCacheAdapter.put(user);

        verify(valueOperations).set("user:" + user.id(), user, TTL);
    }

    @Test
    void get_shouldReturnUser_whenKeyExists() {
        var user = Instancio.create(User.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:" + user.id())).thenReturn(user);

        assertThat(userCacheAdapter.get(user.id())).contains(user);
    }

    @Test
    void get_shouldReturnEmpty_whenKeyAbsent() {
        var id = UUID.randomUUID();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:" + id)).thenReturn(null);

        assertThat(userCacheAdapter.get(id)).isEmpty();
    }

    @Test
    void evict_shouldDeleteKeyFromRedis() {
        var id = UUID.randomUUID();

        userCacheAdapter.evict(id);

        verify(redisTemplate).delete("user:" + id);
    }
}
