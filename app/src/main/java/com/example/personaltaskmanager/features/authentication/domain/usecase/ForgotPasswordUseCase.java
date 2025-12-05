package com.example.personaltaskmanager.features.authentication.domain.usecase;

/**
 * UseCase tạm thời cho Forgot Password.
 * Phiên bản Local chỉ kiểm tra định dạng email.
 *
 * Khi tích hợp Firebase:
 *  - sẽ gọi FirebaseAuth.sendPasswordResetEmail(email)
 */
public class ForgotPasswordUseCase {

    /**
     * Kiểm tra email hợp lệ.
     * Local version cho bài lab.
     */
    public boolean execute(String email) {
        return email != null && email.contains("@");
    }
}
