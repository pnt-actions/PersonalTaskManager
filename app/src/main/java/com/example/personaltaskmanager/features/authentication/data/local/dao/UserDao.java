package com.example.personaltaskmanager.features.authentication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;

/**
 * DAO xử lý CRUD cho bảng Users
 */
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    UserEntity getUserByUsername(String username);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countUsername(String username);

    @Query("SELECT * FROM users LIMIT 1")
    UserEntity getFirstUser();

    @Query("DELETE FROM users")
    void deleteAll();
}
