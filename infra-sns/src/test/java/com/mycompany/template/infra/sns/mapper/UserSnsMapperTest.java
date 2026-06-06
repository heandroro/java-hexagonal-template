package com.mycompany.template.infra.sns.mapper;

import com.mycompany.template.core.domain.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserSnsMapperTest {

    private final UserSnsMapper mapper = new UserSnsMapperImpl();

    @Test
    void should_mapAllFields_when_userIsValid() {
        var user = Instancio.create(User.class);

        var notification = mapper.toNotification(user);

        assertThat(notification.id()).isEqualTo(user.id().toString());
        assertThat(notification.name()).isEqualTo(user.name());
        assertThat(notification.email()).isEqualTo(user.email());
        assertThat(notification.createdAt()).isEqualTo(user.createdAt().toString());
    }

    @Test
    void should_returnNull_when_userIsNull() {
        assertThat(mapper.toNotification(null)).isNull();
    }
}
