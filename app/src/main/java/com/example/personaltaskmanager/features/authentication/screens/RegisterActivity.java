package com.example.personaltaskmanager.features.authentication.screens;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUser, etEmail, etPass, etConfirm;
    private Button btnRegister;
    private TextView tvLogin;

    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_auth_register);

        repo = new AuthRepository(this);

        setupStatusBar();
        initViews();
        setupActions();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.WHITE);

        WindowInsetsControllerCompat wic =
                WindowCompat.getInsetsController(window, window.getDecorView());

        if (wic != null) {
            wic.setAppearanceLightStatusBars(true);
            wic.setAppearanceLightNavigationBars(true);
        }
    }

    private void initViews() {
        etUser = findViewById(R.id.et_register_username);
        etEmail = findViewById(R.id.et_register_email);
        etPass = findViewById(R.id.et_register_password);
        etConfirm = findViewById(R.id.et_register_confirm);

        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_to_login);
    }

    private void setupActions() {

        btnRegister.setOnClickListener(v -> {

            String username = etUser.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPass.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (username.isEmpty()) {
                etUser.setError("Không được để trống");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Không được để trống");
                return;
            }
            if (password.isEmpty()) {
                etPass.setError("Không được để trống");
                return;
            }
            if (!password.equals(confirm)) {
                etConfirm.setError("Mật khẩu xác nhận không trùng!");
                return;
            }

            boolean ok = repo.register(new User(username, email, password));

            if (!ok) {
                Toast.makeText(this,
                        "Tên tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

            finish();
            overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
        });

        // Nhấn "Đăng nhập" → quay lại với animation
        tvLogin.setOnClickListener(v -> {
            finish();
            overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
    }
}
