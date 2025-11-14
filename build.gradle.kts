// Vị trí: D:/SalesApp/build.gradle.kts (File GỐC)

plugins {
    // SỬA 1: Nâng cấp AGP trở lại để khớp với compileSdk = 35
    id("com.android.application") version "8.6.1" apply false

    // SỬA 2: Giữ nguyên Kotlin 1.9.22
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // SỬA 3: Giữ nguyên Hilt 2.51.1
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    // SỬA 4: Giữ nguyên Kapt
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
}

// SỬA 5: Sửa dòng "buildDir" bị cảnh báo (deprecated)
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}