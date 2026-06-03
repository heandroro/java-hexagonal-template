package com.mycompany.template.infra.dynamodb.adapter;

import com.mycompany.template.core.domain.User;
import com.mycompany.template.core.ports.out.UserRepositoryPort;
import com.mycompany.template.infra.dynamodb.entity.UserDynamoDbEntity;
import com.mycompany.template.infra.dynamodb.mapper.UserDynamoDbMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("dynamodb")
public class UserDynamoDbAdapter implements UserRepositoryPort {

    private final DynamoDbTable<UserDynamoDbEntity> table;
    private final UserDynamoDbMapper userDynamoDbMapper;

    public UserDynamoDbAdapter(DynamoDbTable<UserDynamoDbEntity> table, UserDynamoDbMapper userDynamoDbMapper) {
        this.table = table;
        this.userDynamoDbMapper = userDynamoDbMapper;
    }

    @Override
    public User save(User user) {
        UserDynamoDbEntity entity = userDynamoDbMapper.toEntity(user);
        table.putItem(entity);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Key key = Key.builder().partitionValue(id.toString()).build();
        return Optional.ofNullable(table.getItem(key))
                .map(userDynamoDbMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return table.scan().items().stream()
                .anyMatch(entity -> entity.getEmail().equals(email));
    }
}
