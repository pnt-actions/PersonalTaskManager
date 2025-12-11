package com.example.personaltaskmanager.features.authentication.screens;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils; // Thêm thư viện AnimationUtils
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.navigation.NavigationActivity;

public class LoginActivity extends AppCompatActivity {

    // LOGIN
    private EditText etUsername, etPassword;
    private Button btnLogin;

    // REGISTER
    private EditText etRegUser, etRegEmail, etRegPass, etRegConfirm;
    private Button btnRegister;

    // TABS
    private TextView tabLogin, tabRegister;
    private ViewSwitcher switcher;

    // THEM VIEW MOI CHO HIEU UNG DI CHUYEN
    private View tabHighlight;
    private int tabWidth = 0;

    // BIẾN MỚI: Theo dõi form hiện tại để xác định hướng animation
    private int lastDisplayedChild = 0; // 0: Login, 1: Register

    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_auth_login);

        repo = new AuthRepository(this);

        setupStatusBar();
        initViews();
        setupTabs();
        setupLoginActions();
        setupRegisterActions();
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

        // ------------------------
        // TAB SWITCH + SWITCHER
        // ------------------------
        tabLogin = findViewById(R.id.tabLogin);
        tabRegister = findViewById(R.id.tabRegister);
        switcher = findViewById(R.id.viewSwitcherAuth);
        tabHighlight = findViewById(R.id.tabHighlight);

        // ------------------------
        // LOGIN FORM
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // ------------------------
        // REGISTER FORM
        etRegUser = findViewById(R.id.et_register_username);
        etRegEmail = findViewById(R.id.et_register_email);
        etRegPass = findViewById(R.id.et_register_password);
        etRegConfirm = findViewById(R.id.et_register_confirm);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupTabs() {
        // Tinh toan chieu rong tab sau khi layout duoc ve
        tabLogin.post(() -> {
            tabWidth = tabLogin.getWidth();
            tabHighlight.getLayoutParams().width = tabWidth;
            tabHighlight.requestLayout();

            // Set mau chu ban dau cho tab (Login active)
            tabLogin.setTextColor(ContextCompat.getColor(this, R.color.white));
            tabRegister.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
        });

        // LOẠI BỎ LOGIC setBackgroundColor CŨ VÀ THAY BẰNG ANIMATION
        tabLogin.setOnClickListener(v -> {
            switchFormWithAnimation(0); // Chuyển về Login (0)
            animateTab(true); // Login = true
        });

        tabRegister.setOnClickListener(v -> {
            switchFormWithAnimation(1); // Chuyển sang Register (1)
            animateTab(false); // Register = false
        });
    }

    // HAM MOI XU LY ANIMATION VA MAU CHU
    private void animateTab(boolean isLogin) {
        if (tabWidth == 0) return;

        // Tinh toan vi tri dich den (X)
        float targetX = isLogin ? tabLogin.getX() : tabRegister.getX();

        // Di chuyen tabHighlight bang ObjectAnimator
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                tabHighlight, "x", targetX
        );
        animator.setDuration(300);
        animator.start();

        // Thay doi mau chu
        if (isLogin) {
            tabLogin.setTextColor(ContextCompat.getColor(this, R.color.white));
            tabRegister.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
        } else {
            tabLogin.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
            tabRegister.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    // HAM MOI: Xử lý chuyển đổi ViewSwitcher có Animation
    private void switchFormWithAnimation(int newChildIndex) {
        if (newChildIndex == lastDisplayedChild) return; // Không làm gì nếu form không đổi

        if (newChildIndex > lastDisplayedChild) {
            // Chuyển từ Login (0) -> Register (1): Sang PHẢI
            switcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
            switcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        } else {
            // Chuyển từ Register (1) -> Login (0): Sang TRÁI
            switcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
            switcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
        }

        switcher.setDisplayedChild(newChildIndex);
        lastDisplayedChild = newChildIndex;
    }


    private void setupLoginActions() {

        btnLogin.setOnClickListener(v -> {
            // ... (Logic giữ nguyên)
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

            User user = repo.login(username, password);

            if (user == null) {
                Toast.makeText(this,
                        "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, NavigationActivity.class);
            intent.putExtra("role", user.role);
            startActivity(intent);
            finish();
        });
    }

    private void setupRegisterActions() {

        btnRegister.setOnClickListener(v -> {
            // ... (Logic giữ nguyên)
            String username = etRegUser.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String pass = etRegPass.getText().toString().trim();
            String confirm = etRegConfirm.getText().toString().trim();

            if (username.isEmpty()) {
                etRegUser.setError("Không được bỏ trống");
                return;
            }
            if (email.isEmpty()) {
                etRegEmail.setError("Không được bỏ trống");
                return;
            }
            if (pass.isEmpty()) {
                etRegPass.setError("Không được bỏ trống");
                return;
            }
            if (!pass.equals(confirm)) {
                etRegConfirm.setError("Mật khẩu xác nhận không trùng");
                return;
            }

            boolean ok = repo.register(new User(username, email, pass));

            if (!ok) {
                Toast.makeText(this,
                        "Tên người dùng đã tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();

            // AUTO SWITCH về LOGIN sau khi đăng ký thành công
            switchFormWithAnimation(0); // Dùng hàm mới để chuyển về form Login (0)
            animateTab(true);
        });
    }
}