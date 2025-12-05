package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

/**
 * UseCase xử lý đăng ký tài khoản mới.
 * Domain chỉ gọi repo, không biết DB loại gì.
 */
public class RegisterUseCase {

    private final AuthRepository repo;

    public RegisterUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    /**
     * Thực thi hành động đăng ký tài khoản.
     *
     * @param user thông tin người dùng
     * @return true nếu đăng ký thành công
     */
    public boolean execute(User user) {
        return repo.register(user);
    }
}
