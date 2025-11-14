package com.example.salesapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory // <-- Import
import androidx.work.Configuration // <-- Import
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject // <-- Import

@HiltAndroidApp
// SỬA 1: 'Configuration.Provider' là đúng
class SalesAppApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // SỬA 2: Sửa từ 'fun getWork...' thành 'override val...'
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}