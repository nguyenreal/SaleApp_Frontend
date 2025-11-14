package com.example.salesapp.utils

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.salesapp.workers.CartBadgeWorker
import me.leolin.shortcutbadger.ShortcutBadger

/**
 * Utility class để quản lý badge notification
 * Sử dụng ShortcutBadger library để hỗ trợ nhiều launcher
 */
object BadgeManager {
    private const val TAG = "BadgeManager"
    private const val BADGE_UPDATE_WORK = "badge_update_work"

    /**
     * Cập nhật badge trực tiếp (không dùng Worker)
     * Sử dụng khi cần update ngay lập tức
     */
    fun updateBadge(context: Context, count: Int) {
        try {
            if (count > 0) {
                val success = ShortcutBadger.applyCount(context, count)
                if (success) {
                    Log.d(TAG, "Badge updated to: $count")
                } else {
                    Log.w(TAG, "Badge update failed. Launcher may not support badges.")
                }
            } else {
                removeBadge(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating badge: ${e.message}", e)
        }
    }

    /**
     * Xóa badge
     */
    fun removeBadge(context: Context) {
        try {
            ShortcutBadger.removeCount(context)
            Log.d(TAG, "Badge removed")
        } catch (e: Exception) {
            Log.e(TAG, "Error removing badge: ${e.message}", e)
        }
    }

    /**
     * Trigger Worker để update badge (async)
     * Sử dụng khi cần update badge với network call
     */
    fun triggerBadgeUpdateWorker(context: Context) {
        try {
            val workRequest = OneTimeWorkRequestBuilder<CartBadgeWorker>()
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    BADGE_UPDATE_WORK,
                    ExistingWorkPolicy.REPLACE, // Replace work cũ nếu đang chạy
                    workRequest
                )

            Log.d(TAG, "Badge update worker enqueued")
        } catch (e: Exception) {
            Log.e(TAG, "Error enqueueing worker: ${e.message}", e)
        }
    }

    /**
     * Kiểm tra xem launcher có hỗ trợ badge không
     */
    fun isBadgeSupported(context: Context): Boolean {
        return try {
            ShortcutBadger.isBadgeCounterSupported(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking badge support: ${e.message}", e)
            false
        }
    }
}