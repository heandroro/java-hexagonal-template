package com.mycompany.template.infra.dynamodb.config;

import com.mycompany.template.infra.dynamodb.entity.UserDynamoDbEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
@Profile("dynamodb")
public class UserDynamoDbConfig {

    @Bean
    public DynamoDbTable<UserDynamoDbEntity> userDynamoDbTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table("users", TableSchema.fromBean(UserDynamoDbEntity.class));
    }
}
