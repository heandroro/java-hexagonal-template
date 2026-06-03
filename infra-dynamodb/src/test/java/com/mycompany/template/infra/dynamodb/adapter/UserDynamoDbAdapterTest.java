package com.mycompany.template.infra.dynamodb.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.infra.dynamodb.entity.UserDynamoDbEntity;
import com.mycompany.template.infra.dynamodb.mapper.UserDynamoDbMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDynamoDbAdapterTest {

    @Mock
    private DynamoDbTable<UserDynamoDbEntity> table;

    @Mock
    private UserDynamoDbMapper userDynamoDbMapper;

    @InjectMocks
    private UserDynamoDbAdapter userDynamoDbAdapter;

    @Test
    void save_shouldPutItemAndReturnOriginalUser() {
        var user = Instancio.create(User.class);
        var entity = Instancio.create(UserDynamoDbEntity.class);
        when(userDynamoDbMapper.toEntity(user)).thenReturn(entity);

        var result = userDynamoDbAdapter.save(user);

        verify(table).putItem(entity);
        assertThat(result).isEqualTo(user);
    }

    @Test
    void findById_shouldReturnMappedUser_whenItemFound() {
        var user = Instancio.create(User.class);
        var entity = Instancio.create(UserDynamoDbEntity.class);
        when(table.getItem(any(Key.class))).thenReturn(entity);
        when(userDynamoDbMapper.toDomain(entity)).thenReturn(user);

        var result = userDynamoDbAdapter.findById(user.id());

        assertThat(result).contains(user);
    }

    @Test
    void findById_shouldReturnEmpty_whenItemNotFound() {
        when(table.getItem(any(Key.class))).thenReturn(null);

        assertThat(userDynamoDbAdapter.findById(UUID.randomUUID())).isEmpty();
    }
}
