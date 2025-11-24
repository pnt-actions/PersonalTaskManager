package com.example.personaltaskmanager.features.authentication.domain.usecase;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

public class RegisterUseCase {

    private final AuthRepository repo;

    public RegisterUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public boolean execute(User user) {
        return repo.register(user);
    }
}
