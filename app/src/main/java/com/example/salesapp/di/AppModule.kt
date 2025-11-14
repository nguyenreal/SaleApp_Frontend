// Vị trí: .../com/example/salesapp/di/AppModule.kt
package com.example.salesapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.salesapp.data.local.UserPreferencesRepository
import com.example.salesapp.data.remote.AuthInterceptor
import com.example.salesapp.data.remote.api.AuthService
import com.example.salesapp.data.remote.api.CartService
import com.example.salesapp.data.remote.api.ChatService
import com.example.salesapp.data.remote.api.ProductService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

// Đặt tên file lưu trữ
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // <<< THAY THẾ BASE_URL BẰNG ĐỊA CHỈ BACKEND CỦA BẠN >>>
    // Nếu chạy trên máy ảo Android, dùng 10.0.2.2 để trỏ tới localhost của máy thật
    private const val BASE_URL = "http://10.0.2.2:5281/" // << THAY PORT CỦA BẠN

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Cung cấp DataStore (để lưu token)
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductService(retrofit: Retrofit): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    // (Thêm vào bên dưới hàm provideProductService)

    @Provides
    @Singleton
    fun provideCartService(retrofit: Retrofit): CartService {
        return retrofit.create(CartService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatService(retrofit: Retrofit): ChatService {
        return retrofit.create(ChatService::class.java)
    }
}
