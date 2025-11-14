// Vị trí: .../com/example/salesapp/utils/NotificationHelper.kt
package com.example.salesapp.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.salesapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val CHANNEL_ID = "cart_channel"
    private val NOTIFICATION_ID = 1 // ID cố định để cập nhật badge

    init {
        createNotificationChannel()
    }

    // 1. Tạo Kênh Thông Báo (Chỉ chạy 1 lần)
    private fun createNotificationChannel() {
        // Chỉ cần tạo channel trên API 26+ (Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Giỏ Hàng"
            val descriptionText = "Thông báo cập nhật giỏ hàng"
            // Mức độ quan trọng: LOW (Thấp) vì chúng ta không muốn nó kêu
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                // Quan trọng: Tắt âm thanh và rung
                setSound(null, null)
                enableVibration(false)
            }

            // Đăng ký channel với hệ thống
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 2. Hàm cập nhật Badge
    fun updateCartBadge(itemCount: Int) {
        // Kiểm tra quyền (mặc dù đã xin ở Activity, nhưng an toàn hơn)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Không có quyền, không làm gì cả
                return
            }
        }

        with(NotificationManagerCompat.from(context)) {
            if (itemCount == 0) {
                // Nếu giỏ hàng trống, hủy thông báo (sẽ xóa luôn badge)
                cancel(NOTIFICATION_ID)
                return
            }

            // Tạo một thông báo "im lặng"
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Icon nhỏ (bắt buộc)
                .setContentTitle("Giỏ Hàng")
                .setContentText("Bạn có $itemCount sản phẩm trong giỏ hàng.")
                .setNumber(itemCount) // <-- Đây là chìa khóa để hiện Badge
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true) // Chỉ thông báo 1 lần
                .setAutoCancel(true) // Tự xóa khi click (nếu có PendingIntent)
            // .setSilent(true) // Đã set importance = LOW nên không cần

            // Hiển thị thông báo (sẽ cập nhật badge)
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    // 3. Hàm xóa Badge (khi người dùng đăng xuất)
    fun clearCartBadge() {
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_ID)
        }
    }
}