package com.example.personaltaskmanager.features.authentication.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.authentication.domain.usecase.*;

/**
 * AuthViewModel
 * ----------------
 * Trung gian giữa UI và UseCases.
 * Không chứa logic UI và không thao tác DB trực tiếp.
 */
public class AuthViewModel extends ViewModel {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final ForgotPasswordUseCase forgotUseCase;

    public AuthViewModel(Context context) {

        AuthRepository repo = new AuthRepository(context);

        loginUseCase = new LoginUseCase(repo);
        registerUseCase = new RegisterUseCase(repo);
        logoutUseCase = new LogoutUseCase(repo);
        getCurrentUserUseCase = new GetCurrentUserUseCase(repo);
        forgotUseCase = new ForgotPasswordUseCase();
    }

    // ---- CẬP NHẬT CHO FIREBASE ASYNC ----
    public void login(String email, String password, AuthRepository.AuthCallback callback) {
        loginUseCase.execute(email, password, callback);
    }

    public void register(String username, String email, String password, AuthRepository.AuthCallback callback) {
        registerUseCase.execute(username, email, password, callback);
    }

    public void logout() {
        logoutUseCase.execute();
    }

    public User getCurrent() {
        return getCurrentUserUseCase.execute();
    }

    public boolean forgotPassword(String email) {
        return forgotUseCase.execute(email);
    }
}
