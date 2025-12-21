package com.example.personaltaskmanager.features.profile_settings.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.authentication.data.local.AuthDatabase;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

/**
 * ProfileSettingsActivity
 * -----------------------
 * Màn hình quản lý hồ sơ người dùng.
 */
public class ProfileSettingsActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnSaveProfile, btnChangePassword;
    private TextView tvUsername;
    private AuthRepository authRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_profile_settings_main);

        authRepo = new AuthRepository(this);

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tv_profile_username);
        edtUsername = findViewById(R.id.edt_profile_username);
        edtEmail = findViewById(R.id.edt_profile_email);
        edtCurrentPassword = findViewById(R.id.edt_profile_current_password);
        edtNewPassword = findViewById(R.id.edt_profile_new_password);
        edtConfirmPassword = findViewById(R.id.edt_profile_confirm_password);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private void loadUserData() {
        com.example.personaltaskmanager.features.authentication.data.model.User user = authRepo.getCurrentUser();
        if (user != null) {
            tvUsername.setText("Xin chào, " + user.username + "!");
            edtUsername.setText(user.username);
            edtEmail.setText(user.email);
        }
    }

    private void setupListeners() {
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void saveProfile() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Tên người dùng không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.personaltaskmanager.features.authentication.data.model.User currentUser = authRepo.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin trong database
        AuthDatabase db = AuthDatabase.getInstance(this);
        UserEntity userEntity = db.userDao().getUserByUsername(currentUser.username);
        
        if (userEntity != null) {
            // Kiểm tra username mới có trùng không (nếu thay đổi)
            if (!username.equals(currentUser.username)) {
                if (db.userDao().countUsername(username) > 0) {
                    Toast.makeText(this, "Tên người dùng đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            userEntity.username = username;
            userEntity.email = email;
            db.userDao().updateUser(userEntity);

            // Cập nhật SharedPreferences nếu username thay đổi
            if (!username.equals(currentUser.username)) {
                SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
                prefs.edit().putString("current_user", username).apply();
            }

            Toast.makeText(this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void changePassword() {
        String currentPassword = edtCurrentPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.personaltaskmanager.features.authentication.data.model.User currentUser = authRepo.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu hiện tại
        AuthDatabase db = AuthDatabase.getInstance(this);
        UserEntity userEntity = db.userDao().getUserByUsername(currentUser.username);
        
        if (userEntity != null && userEntity.password.equals(currentPassword)) {
            userEntity.password = newPassword;
            db.userDao().updateUser(userEntity);
            Toast.makeText(this, "Đã đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            
            // Clear password fields
            edtCurrentPassword.setText("");
            edtNewPassword.setText("");
            edtConfirmPassword.setText("");
        } else {
            Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
        }
    }
}

