package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.authentication.data.model.User;

/**
 * UseCase xử lý logic đăng nhập.
 * Domain Layer KHÔNG biết Repository dùng local hay Firebase.
 * 
 * Với Firebase, đăng nhập là async nên cần callback.
 */
public class LoginUseCase {

    private final AuthRepository repo;

    public LoginUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    /**
     * Thực thi hành động đăng nhập (async với Firebase).
     *
     * @param email email đăng nhập (Firebase dùng email)
     * @param password mật khẩu
     * @param callback callback để xử lý kết quả
     */
    public void execute(String email, String password, AuthRepository.AuthCallback callback) {
        repo.login(email, password, callback);
    }
}
