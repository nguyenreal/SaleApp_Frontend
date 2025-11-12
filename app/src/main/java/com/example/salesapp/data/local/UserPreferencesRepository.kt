// Vị trí: .../com/example/salesapp/data/local/UserPreferencesRepository.kt
package com.example.salesapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Định nghĩa key để lưu token
    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        // Bạn có thể thêm key cho role, username... nếu muốn
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    // Lấy token (dưới dạng Flow, tự động cập nhật)
    val authToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN]
        }

    // Lưu token và role (sau khi login thành công)
    suspend fun saveAuthInfo(token: String, role: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
            preferences[PreferencesKeys.USER_ROLE] = role
        }
    }

    // Xóa token (khi logout)
    suspend fun clearAuthInfo() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.AUTH_TOKEN)
            preferences.remove(PreferencesKeys.USER_ROLE)
        }
    }
}