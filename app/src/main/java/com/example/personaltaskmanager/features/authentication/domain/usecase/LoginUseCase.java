package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

/**
 * UseCase xử lý logic đăng nhập.
 * Domain Layer KHÔNG biết Repository dùng local hay Firebase.
 */
public class LoginUseCase {

    private final AuthRepository repo;

    public LoginUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    /**
     * Thực thi hành động đăng nhập.
     *
     * @param username tên đăng nhập
     * @param password mật khẩu
     * @return true nếu thành công
     */
    public boolean execute(String username, String password) {
        return repo.login(username, password);
    }
}
