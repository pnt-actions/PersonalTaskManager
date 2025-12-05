package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

/**
 * UseCase xử lý đăng xuất người dùng.
 * Xoá dữ liệu đăng nhập local (SharedPrefs).
 */
public class LogoutUseCase {

    private final AuthRepository repo;

    public LogoutUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    /**
     * Thực thi logout.
     */
    public void execute() {
        repo.logout();
    }
}
