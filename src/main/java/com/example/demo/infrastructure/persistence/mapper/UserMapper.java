package com.example.demo.infrastructure.persistence.mapper;

import com.example.demo.domain.model.User;
import com.example.demo.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(User domain) {
        return new UserEntity(
                domain.getId(),
                domain.getUsername(),
                domain.getPassword()

        );
    }

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword()
        );
    }
}
