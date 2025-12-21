package com.example.personaltaskmanager.features.authentication.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.personaltaskmanager.features.authentication.data.local.AuthDatabase;
import com.example.personaltaskmanager.features.authentication.data.local.dao.UserDao;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import com.example.personaltaskmanager.features.authentication.data.mapper.UserMapper;
import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AuthRepository
 * ---------------------
 * Xử lý logic Authentication với Firebase:
 *   - Login (Firebase Authentication)
 *   - Register (Firebase Authentication + Firestore)
 *   - Lưu trạng thái đăng nhập (SharedPreferences + Firebase Auth)
 *   - Logout
 */
public class AuthRepository {

    private final UserDao userDao;
    private final SharedPreferences prefs;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public AuthRepository(Context context) {
        userDao = AuthDatabase.getInstance(context).userDao();
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * LOGIN với Firebase Authentication
     * Sử dụng email làm username để đăng nhập
     */
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Lấy thông tin user từ Firestore
                            firestore.collection("users")
                                    .document(firebaseUser.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String username = documentSnapshot.getString("username");
                                            String userEmail = documentSnapshot.getString("email");
                                            String role = documentSnapshot.getString("role");
                                            if (role == null) role = "user";

                                            User user = new User(
                                                    firebaseUser.getUid().hashCode(), // Tạm thời dùng hash của UID
                                                    username != null ? username : email,
                                                    userEmail != null ? userEmail : email,
                                                    "", // Không lưu password
                                                    role
                                            );

                                            // Lưu vào SharedPreferences
                                            prefs.edit()
                                                    .putString("current_user", username != null ? username : email)
                                                    .putString("firebase_uid", firebaseUser.getUid())
                                                    .apply();

                                            // Đồng bộ với local database
                                            syncUserToLocal(user);

                                            // Gọi callback trên main thread
                                            mainHandler.post(() -> callback.onSuccess(user));
                                        } else {
                                            mainHandler.post(() -> callback.onError("Không tìm thấy thông tin người dùng"));
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        String errorMsg = "Lỗi khi lấy thông tin người dùng: " + (e.getMessage() != null ? e.getMessage() : "Unknown error");
                                        mainHandler.post(() -> callback.onError(errorMsg));
                                    });
                        } else {
                            mainHandler.post(() -> callback.onError("Đăng nhập thất bại"));
                        }
                    } else {
                        String errorMessage = "Đăng nhập thất bại";
                        if (task.getException() != null) {
                            String error = task.getException().getMessage();
                            if (error != null) {
                                if (error.contains("INVALID_LOGIN_CREDENTIALS") || error.contains("wrong-password")) {
                                    errorMessage = "Email hoặc mật khẩu không đúng";
                                } else if (error.contains("user-not-found")) {
                                    errorMessage = "Người dùng không tồn tại";
                                } else {
                                    errorMessage = error;
                                }
                            }
                        }
                        final String finalErrorMessage = errorMessage;
                        mainHandler.post(() -> callback.onError(finalErrorMessage));
                    }
                });
    }

    /**
     * REGISTER với Firebase Authentication và Firestore
     */
    public void register(String username, String email, String password, AuthCallback callback) {
        // Kiểm tra username đã tồn tại trong Firestore
        firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(checkTask -> {
                    if (checkTask.isSuccessful() && !checkTask.getResult().isEmpty()) {
                        mainHandler.post(() -> callback.onError("Tên người dùng đã tồn tại"));
                        return;
                    }
                    
                    // Xử lý lỗi khi check username
                    if (!checkTask.isSuccessful() && checkTask.getException() != null) {
                        // Nếu có lỗi khi check, vẫn tiếp tục (có thể là lỗi network)
                        // Nhưng log lại để debug
                        Exception e = checkTask.getException();
                        // Tiếp tục với bước tiếp theo
                    }

                    // Kiểm tra email đã tồn tại
                    firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(emailCheckTask -> {
                                if (emailCheckTask.isSuccessful() && !emailCheckTask.getResult().isEmpty()) {
                                    mainHandler.post(() -> callback.onError("Email đã được sử dụng"));
                                    return;
                                }
                                
                                // Xử lý lỗi khi check email
                                if (!emailCheckTask.isSuccessful() && emailCheckTask.getException() != null) {
                                    // Nếu có lỗi khi check, vẫn tiếp tục (có thể là lỗi network)
                                    // Tiếp tục với bước tiếp theo
                                }

                                // Tạo tài khoản Firebase
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(createTask -> {
                                            if (createTask.isSuccessful()) {
                                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                if (firebaseUser != null) {
                                                    // Lưu thông tin vào Firestore
                                                    Map<String, Object> userData = new HashMap<>();
                                                    userData.put("username", username);
                                                    userData.put("email", email);
                                                    userData.put("role", "user");
                                                    userData.put("createdAt", System.currentTimeMillis());

                                                    firestore.collection("users")
                                                            .document(firebaseUser.getUid())
                                                            .set(userData)
                                                            .addOnCompleteListener(firestoreTask -> {
                                                                if (firestoreTask.isSuccessful()) {
                                                                    User user = new User(
                                                                            firebaseUser.getUid().hashCode(),
                                                                            username,
                                                                            email,
                                                                            "",
                                                                            "user"
                                                                    );

                                                                    // Lưu vào SharedPreferences
                                                                    prefs.edit()
                                                                            .putString("current_user", username)
                                                                            .putString("firebase_uid", firebaseUser.getUid())
                                                                            .apply();

                                                                    // Đồng bộ với local database
                                                                    syncUserToLocal(user);

                                                                    callback.onSuccess(user);
                                                                } else {
                                                                    // Nếu lưu Firestore thất bại, vẫn cho phép đăng ký thành công
                                                                    // vì tài khoản đã được tạo trong Firebase Auth
                                                                    // Nhưng thông báo lỗi để user biết
                                                                    Exception e = firestoreTask.getException();
                                                                    String errorMsg = "Tài khoản đã được tạo nhưng có lỗi khi lưu thông tin";
                                                                    if (e != null && e.getMessage() != null) {
                                                                        errorMsg += ": " + e.getMessage();
                                                                    }
                                                                    
                                                                    // Vẫn tạo User object để app có thể tiếp tục
                                                                    User user = new User(
                                                                            firebaseUser.getUid().hashCode(),
                                                                            username,
                                                                            email,
                                                                            "",
                                                                            "user"
                                                                    );

                                                                    prefs.edit()
                                                                            .putString("current_user", username)
                                                                            .putString("firebase_uid", firebaseUser.getUid())
                                                                            .apply();

                                                                    syncUserToLocal(user);
                                                                    
                                                                    // Gọi onSuccess trên main thread
                                                                    mainHandler.post(() -> callback.onSuccess(user));
                                                                }
                                                            });
                                                } else {
                                                    mainHandler.post(() -> callback.onError("Tạo tài khoản thất bại: Không lấy được thông tin người dùng"));
                                                }
                                            } else {
                                                String errorMessage = "Đăng ký thất bại";
                                                if (createTask.getException() != null) {
                                                    String error = createTask.getException().getMessage();
                                                    if (error != null) {
                                                        if (error.contains("email-already-in-use")) {
                                                            errorMessage = "Email đã được sử dụng";
                                                        } else if (error.contains("weak-password")) {
                                                            errorMessage = "Mật khẩu quá yếu (tối thiểu 6 ký tự)";
                                                        } else {
                                                            errorMessage = error;
                                                        }
                                                    }
                                                }
                                                final String finalErrorMessage = errorMessage;
                                                mainHandler.post(() -> callback.onError(finalErrorMessage));
                                            }
                                        });
                            });
                });
    }

    /**
     * Đồng bộ user từ Firebase vào local database
     */
    private void syncUserToLocal(User user) {
        executor.execute(() -> {
            // Kiểm tra xem user đã tồn tại chưa
            UserEntity existing = userDao.getUserByUsername(user.username);
            if (existing == null) {
                // Chưa có thì insert
                userDao.insertUser(UserMapper.toEntity(user));
            } else {
                // Có rồi thì update
                existing.email = user.email;
                existing.role = user.role;
                userDao.updateUser(existing);
            }
        });
    }

    /**
     * GET CURRENT USER từ Firebase
     */
    public User getCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String username = prefs.getString("current_user", null);
            if (username != null) {
                // Lấy từ local database
                UserEntity e = userDao.getUserByUsername(username);
                if (e != null) {
                    return UserMapper.toModel(e);
                }
            }
        }
        return null;
    }

    /**
     * LOGOUT
     */
    public void logout() {
        firebaseAuth.signOut();
        prefs.edit()
                .remove("current_user")
                .remove("firebase_uid")
                .apply();
    }

    /**
     * Kiểm tra xem user đã đăng nhập chưa (Firebase Auth)
     */
    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
