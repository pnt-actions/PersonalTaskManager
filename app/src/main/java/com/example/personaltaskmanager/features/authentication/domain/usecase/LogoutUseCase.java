package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

public class LogoutUseCase {

    private final AuthRepository repo;

    public LogoutUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute() {
        repo.logout();
    }
}
