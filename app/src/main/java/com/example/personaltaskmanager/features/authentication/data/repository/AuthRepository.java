package com.example.personaltaskmanager.features.authentication.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.personaltaskmanager.features.authentication.data.local.AuthDatabase;
import com.example.personaltaskmanager.features.authentication.data.local.dao.UserDao;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import com.example.personaltaskmanager.features.authentication.data.mapper.UserMapper;
import com.example.personaltaskmanager.features.authentication.data.model.User;

/**
 * Repository trung gian kết nối UseCase <-> Database
 */
public class AuthRepository {

    private final UserDao userDao;
    private final SharedPreferences prefs;

    public AuthRepository(Context context) {
        userDao = AuthDatabase.getInstance(context).userDao();
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
    }

    public boolean login(String username, String password) {
        UserEntity u = userDao.getUserByUsername(username);
        boolean ok = (u != null && u.password.equals(password));

        if (ok) {
            prefs.edit()
                    .putString("current_user", username)
                    .apply();
        }
        return ok;
    }

    public boolean register(User user) {
        // Kiểm tra trùng username
        if (userDao.countUsername(user.username) > 0) {
            return false;
        }

        userDao.insertUser(UserMapper.toEntity(user));
        return true;
    }

    public User getCurrentUser() {
        String username = prefs.getString("current_user", null);
        if (username == null) return null;
        return UserMapper.toModel(userDao.getUserByUsername(username));
    }

    public void logout() {
        prefs.edit().remove("current_user").apply();
    }
}
