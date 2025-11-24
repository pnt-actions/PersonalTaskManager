package com.example.personaltaskmanager.features.authentication.data.mapper;

import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import com.example.personaltaskmanager.features.authentication.data.model.User;

/**
 * Mapper chuyển giữa UserEntity (Room) và User (Domain Model).
 *
 * Mục tiêu:
 *  - Tách biệt domain model khỏi entity của Room
 *  - Tránh leak logic database vào domain layer
 */
public class UserMapper {

    /**
     * Convert từ Entity (lưu trong DB) thành Model dùng trong app.
     */
    public static User toModel(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.username,
                entity.email,
                entity.password
        );
    }

    /**
     * Convert từ Model (User) sang Entity để lưu DB.
     */
    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        return new UserEntity(
                user.username,
                user.email,
                user.password
        );
    }
}
