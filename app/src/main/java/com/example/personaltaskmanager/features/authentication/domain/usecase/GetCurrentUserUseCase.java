package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

public class GetCurrentUserUseCase {

    private final AuthRepository repo;

    public GetCurrentUserUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public User execute() {
        return repo.getCurrentUser();
    }
}
