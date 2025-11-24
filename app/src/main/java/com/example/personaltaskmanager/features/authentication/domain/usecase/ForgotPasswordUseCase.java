package com.example.personaltaskmanager.features.authentication.domain.usecase;

public class ForgotPasswordUseCase {

    public boolean execute(String email) {
        return email != null && email.contains("@");
    }
}
