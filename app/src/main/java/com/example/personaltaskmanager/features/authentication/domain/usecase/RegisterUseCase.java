package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

/**
 * UseCase xử lý đăng ký tài khoản mới.
 * Domain chỉ gọi repo, không biết DB loại gì.
 * 
 * Với Firebase, đăng ký là async nên cần callback.
 */
public class RegisterUseCase {

    private final AuthRepository repo;

    public RegisterUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    /**
     * Thực thi hành động đăng ký tài khoản (async với Firebase).
     *
     * @param username tên người dùng
     * @param email email
     * @param password mật khẩu
     * @param callback callback để xử lý kết quả
     */
    public void execute(String username, String email, String password, AuthRepository.AuthCallback callback) {
        repo.register(username, email, password, callback);
    }
}
