package com.example.personaltaskmanager.features.authentication.screens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.task_manager.screens.TaskManagerMainActivity;

/**
 * LoginActivity
 * ----------------
 * Màn hình đăng nhập cục bộ (Offline only).
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private Switch switchTheme;

    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_auth_login);

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
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        switchTheme = findViewById(R.id.switch_theme);
    }

    private void setupActions() {

        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                etUsername.setError("Không được bỏ trống");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Không được bỏ trống");
                return;
            }

            boolean ok = repo.login(username, password);

            if (!ok) {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, TaskManagerMainActivity.class);
            startActivity(i);
            finish();
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
