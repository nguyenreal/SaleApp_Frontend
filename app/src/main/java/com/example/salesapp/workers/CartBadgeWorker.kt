package com.example.salesapp.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.salesapp.MainActivity
import com.example.salesapp.R // Quan trọng!
import com.example.salesapp.data.repository.CartRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CartBadgeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cartRepository: CartRepository // Hilt inject Repository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "cart_channel" // Phải khớp với ID trong MainActivity
        const val NOTIFICATION_ID = 1337
    }

    override suspend fun doWork(): Result {
        // 1. Gọi Repository để lấy giỏ hàng
        val cartResult = cartRepository.getMyCart()

        cartResult.onSuccess { cart ->
            val itemCount = cart.items.sumOf { it.quantity } // Lấy tổng số lượng

            if (itemCount > 0) {
                // 2. Nếu có hàng, gửi thông báo CÓ SỐ
                sendBadgeNotification(itemCount)
            } else {
                // 3. Nếu không có hàng, xóa thông báo (xóa badge)
                clearBadgeNotification()
            }
            return Result.success()

        }.onFailure {
            return Result.retry() // Thử lại nếu lỗi mạng
        }
        return Result.failure()
    }

    private fun sendBadgeNotification(itemCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // <-- THAY BẰNG ICON THÔNG BÁO CỦA BẠN
            .setContentTitle("Giỏ hàng của bạn")
            .setContentText("Bạn có $itemCount sản phẩm đang chờ.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setNumber(itemCount)   // <-- DÒNG QUAN TRỌNG NHẤT (HIỂN THỊ SỐ)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    private fun clearBadgeNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_ID)
        }
    }
}