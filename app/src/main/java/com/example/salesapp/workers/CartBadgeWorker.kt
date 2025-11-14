package com.example.salesapp.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.salesapp.MainActivity
import com.example.salesapp.R
import com.example.salesapp.data.repository.CartRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.leolin.shortcutbadger.ShortcutBadger

/**
 * 🎯 CartBadgeWorker - Emulator Optimized Version
 *
 * Strategy: Sử dụng ONGOING NOTIFICATION để hiển thị badge count
 * Vì Android emulator không có launcher hỗ trợ app icon badge,
 * chúng ta dùng notification với .setNumber() và .setOngoing(true)
 *
 * Ưu điểm:
 * ✅ Hoạt động 100% trên emulator
 * ✅ Hiển thị số lượng items rõ ràng
 * ✅ Luôn visible, không thể swipe xóa
 * ✅ Tap để mở giỏ hàng
 * ✅ Tự động xóa khi giỏ trống
 */
@HiltWorker
class CartBadgeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cartRepository: CartRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "CartBadgeWorker"
        const val CHANNEL_ID = "cart_channel"
        const val NOTIFICATION_ID = 1337
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "🚀 CartBadgeWorker started!")

        return try {
            val cartResult = cartRepository.getMyCart()

            cartResult.fold(
                onSuccess = { cart ->
                    val itemCount = cart.items.sumOf { it.quantity }
                    Log.d(TAG, "✅ Cart fetched successfully. Item count: $itemCount")

                    if (itemCount > 0) {
                        // Update badge (for real devices)
                        updateBadge(itemCount)

                        // Show ongoing notification (for emulator & real devices)
                        showOngoingNotification(itemCount)
                    } else {
                        // Clear everything when cart is empty
                        clearAll()
                    }

                    Log.d(TAG, "✅ Worker completed successfully")
                    Result.success()
                },
                onFailure = { exception ->
                    Log.e(TAG, "❌ Failed to fetch cart: ${exception.message}")
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Worker failed with exception: ${e.message}", e)
            Result.failure()
        }
    }

    /**
     * Update app icon badge (works on real devices with supported launchers)
     */
    private fun updateBadge(count: Int) {
        try {
            val success = ShortcutBadger.applyCount(context, count)
            if (success) {
                Log.d(TAG, "🎯 Badge updated to: $count")
            } else {
                Log.w(TAG, "⚠️ Badge not supported. Using notification.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Badge error: ${e.message}. Using notification fallback.")
        }
    }

    /**
     * 🔥 MAIN STRATEGY: Show ongoing notification with badge count
     * This notification:
     * - Cannot be dismissed by user (.setOngoing(true))
     * - Shows item count as badge (.setNumber())
     * - Opens cart when tapped
     * - Only disappears when cart is empty
     */
    private fun showOngoingNotification(itemCount: Int) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "⚠️ POST_NOTIFICATIONS permission not granted")
            return
        }

        try {
            // Intent to open cart screen
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("openCart", true)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Build notification text
            val itemText = if (itemCount == 1) "1 sản phẩm" else "$itemCount sản phẩm"

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Giỏ hàng")
                .setContentText("$itemText đang chờ")
                .setPriority(NotificationCompat.PRIORITY_LOW) // LOW = không làm phiền
                .setContentIntent(pendingIntent)

                // 🔥 KEY SETTINGS for ongoing notification
                .setOngoing(true) // ⭐ Không thể swipe xóa
                .setAutoCancel(false) // ⭐ Không tự xóa khi tap
                .setOnlyAlertOnce(true) // Không rung/âm thanh khi update

                // 🔥 BADGE COUNT
                .setNumber(itemCount) // ⭐⭐⭐ Hiển thị số
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

                // UI improvements
                .setShowWhen(false) // Không hiển thị thời gian
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notification.build())

            Log.d(TAG, "✅ Ongoing notification shown with count: $itemCount")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error showing notification: ${e.message}", e)
        }
    }

    /**
     * Clear badge and notification when cart is empty
     */
    private fun clearAll() {
        try {
            // Clear app icon badge
            ShortcutBadger.removeCount(context)

            // Cancel notification
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)

            Log.d(TAG, "🧹 All cleared (cart is empty)")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error clearing: ${e.message}", e)
        }
    }
}