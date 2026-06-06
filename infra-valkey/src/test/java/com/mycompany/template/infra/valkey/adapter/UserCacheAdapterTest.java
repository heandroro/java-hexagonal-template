package com.mycompany.template.infra.valkey.adapter;

import com.mycompany.template.core.domain.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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

    @Nested
    class Put {

        @Test
        void should_storeUserWithConfiguredTtl() {
            var user = Instancio.create(User.class);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);

            userCacheAdapter.put(user);

            then(valueOperations).should().set("user:" + user.id(), user, TTL);
        }
    }

    @Nested
    class Get {

        @Test
        void should_returnUser_when_keyExists() {
            var user = Instancio.create(User.class);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("user:" + user.id())).willReturn(user);

            assertThat(userCacheAdapter.get(user.id())).contains(user);
        }

        @Test
        void should_returnEmpty_when_keyAbsent() {
            var id = UUID.randomUUID();
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("user:" + id)).willReturn(null);

            assertThat(userCacheAdapter.get(id)).isEmpty();
        }
    }

    @Nested
    class Evict {

        @Test
        void should_deleteKeyFromRedis() {
            var id = UUID.randomUUID();

            userCacheAdapter.evict(id);

            then(redisTemplate).should().delete("user:" + id);
        }
    }
}
