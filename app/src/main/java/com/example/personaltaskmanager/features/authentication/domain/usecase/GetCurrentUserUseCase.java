package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

/**
 * UseCase lấy người dùng hiện tại từ Local Storage.
 */
public class GetCurrentUserUseCase {

    private final AuthRepository repo;

    public GetCurrentUserUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    /**
     * @return User nếu đã đăng nhập, null nếu chưa.
     */
    public User execute() {
        return repo.getCurrentUser();
    }
}
