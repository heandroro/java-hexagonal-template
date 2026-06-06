package com.mycompany.template.infra.mariadb.repository;

import com.mycompany.template.infra.mariadb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);
}
