package com.example.personaltaskmanager.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.authentication.screens.AuthSplashActivity;
import com.example.personaltaskmanager.features.navigation.NavigationActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * MainActivity
 * -----------------------
 * Điểm vào chính của ứng dụng.
 *
 * Luồng chạy hiện tại:
 *   App → MainActivity → AuthSplashActivity → Login → NavigationActivity
 *
 * Chức năng:
 *   - Kiểm tra trạng thái đăng nhập (SharedPreferences)
 *   - Nếu có user → vào NavigationActivity
 *   - Nếu không → vào AuthSplash (Splash → Login)
 *
 * Sau này có thể:
 *   - Thay SharedPreferences = FirebaseAuth
 *   - Auto-login khi token còn hạn
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ==========================================================
        // 1) DEMO MODE (TEST CALENDAR) — giữ nguyên theo thiết kế cũ
        // ==========================================================

        boolean DEMO_CALENDAR = false; // ← bật khi bạn muốn bỏ qua Login

        if (DEMO_CALENDAR) {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
            return;
        }

        // ==========================================================
        // 2) LOGIN CHECK — kiểm tra Firebase Auth state
        // ==========================================================

        AuthRepository authRepo = new AuthRepository(this);
        
        // Kiểm tra Firebase Auth trước
        if (authRepo.isLoggedIn()) {
            // Đã đăng nhập với Firebase → vào NavigationActivity
            startActivity(new Intent(this, NavigationActivity.class));
        } else {
            // Chưa đăng nhập → đi vào màn auth splash
            startActivity(new Intent(this, AuthSplashActivity.class));
        }

        // Kết thúc MainActivity để không quay lại được bằng nút Back
        finish();
    }
}
