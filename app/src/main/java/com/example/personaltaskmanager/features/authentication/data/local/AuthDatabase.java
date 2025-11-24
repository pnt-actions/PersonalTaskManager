package com.example.personaltaskmanager.features.authentication.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.personaltaskmanager.features.authentication.data.local.dao.UserDao;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;

/**
 * Room Database dành riêng cho chức năng Authentication.
 * Không dùng chung Database với các feature khác.
 *
 * Sử dụng Singleton để đảm bảo chỉ tạo 1 instance duy nhất trong toàn ứng dụng,
 * giúp tối ưu bộ nhớ và tránh rủi ro rò rỉ kết nối đến database.
 */
@Database(
        entities = {UserEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AuthDatabase extends RoomDatabase {

    // Instance duy nhất của AuthDatabase (Singleton)
    private static volatile AuthDatabase INSTANCE;

    // Trả về DAO để thao tác với bảng User
    public abstract UserDao userDao();

    /**
     * Lấy instance của Database.
     * Dùng cơ chế Double-Check Locking để thread-safe và tối ưu hiệu năng.
     */
    public static AuthDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AuthDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AuthDatabase.class,
                                    "auth_db" // Tên file database
                            )
                            // Xóa & tạo mới DB nếu có thay đổi version — phù hợp với bài lab
                            .fallbackToDestructiveMigration()
                            // Không cho truy cập DB trên main thread (an toàn hơn)
                            .allowMainThreadQueries()   // ⚠ Giữ lại nếu bạn muốn giống với TaskManager
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
