package com.mycompany.template.infra.dynamodb.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.dynamodb.entity.UserDynamoDbEntity;
import com.mycompany.template.infra.dynamodb.mapper.UserDynamoDbMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserDynamoDbAdapterTest {

    @Mock
    private DynamoDbTable<UserDynamoDbEntity> table;

    @Mock
    private UserDynamoDbMapper userDynamoDbMapper;

    @Mock
    private PageIterable<UserDynamoDbEntity> scanIterable;

    @InjectMocks
    private UserDynamoDbAdapter userDynamoDbAdapter;

    @Nested
    class Save {

        @Test
        void should_putItemAndReturnOriginalUser() {
            var user = Instancio.create(User.class);
            var entity = Instancio.create(UserDynamoDbEntity.class);
            given(userDynamoDbMapper.toEntity(user)).willReturn(entity);

            var result = userDynamoDbAdapter.save(user);

            then(table).should().putItem(entity);
            assertThat(result).isEqualTo(user);
        }
    }

    @Nested
    class FindById {

        @Test
        void should_returnMappedUser_when_itemFound() {
            var user = Instancio.create(User.class);
            var entity = Instancio.create(UserDynamoDbEntity.class);
            given(table.getItem(any(Key.class))).willReturn(entity);
            given(userDynamoDbMapper.toDomain(entity)).willReturn(user);

            assertThat(userDynamoDbAdapter.findById(user.id())).contains(user);
        }

        @Test
        void should_returnEmpty_when_itemNotFound() {
            given(table.getItem(any(Key.class))).willReturn(null);

            assertThat(userDynamoDbAdapter.findById(UUID.randomUUID())).isEmpty();
        }
    }

    @Nested
    class ExistsByEmail {

        @Test
        void should_returnTrue_when_emailFound() {
            var entity = Instancio.create(UserDynamoDbEntity.class);
            entity.setEmail("found@example.com");
            SdkIterable<UserDynamoDbEntity> items = () -> List.of(entity).iterator();
            given(table.scan()).willReturn(scanIterable);
            given(scanIterable.items()).willReturn(items);

            assertThat(userDynamoDbAdapter.existsByEmail("found@example.com")).isTrue();
        }

        @Test
        void should_returnFalse_when_emailNotFound() {
            SdkIterable<UserDynamoDbEntity> empty = List.<UserDynamoDbEntity>of()::iterator;
            given(table.scan()).willReturn(scanIterable);
            given(scanIterable.items()).willReturn(empty);

            assertThat(userDynamoDbAdapter.existsByEmail("missing@example.com")).isFalse();
        }
    }
}
