package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

public class LoginUseCase {

    private final AuthRepository repo;

    public LoginUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public boolean execute(String username, String password) {
        return repo.login(username, password);
    }
}
