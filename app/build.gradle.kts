// Vị trí: SalesApp/app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

kapt {
    correctErrorTypes = true
}

android {
    // <<< ĐẢM BẢO DÒNG NÀY ĐÚNG VỚI PACKAGE CỦA BẠN >>>
    namespace = "com.example.salesapp"
    compileSdk = 34 // <<< Chúng ta giữ nguyên SDK 34

    defaultConfig {
        // <<< ĐẢM BẢO DÒNG NÀY ĐÚNG VỚI PACKAGE CỦA BẠN >>>
        applicationId = "com.example.salesapp"
        minSdk = 26
        targetSdk = 34 // <<< Giữ nguyên SDK 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Khớp với Kotlin 1.9.22
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

//
// <<< KHỐI DEPENDENCIES ĐÃ ĐƯỢC SỬA LẠI HOÀN TOÀN >>>
//
dependencies {

    // --- Thư viện mặc định (Đã hạ version cho tương thích) ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // --- CRITICAL FIX: Hạ cấp Compose BOM để tương thích với SDK 34 ---
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // --- Các thư viện của chúng ta (Đã hạ cấp) ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // <<< CRITICAL FIX

    // --- Hilt (Giữ nguyên) ---
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- Network (Giữ nguyên) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Image (Giữ nguyên) ---
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- SignalR (Giữ nguyên) ---
    implementation("com.microsoft.signalr:signalr:8.0.0")

    // --- DataStore (Giữ nguyên) ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Thư viện Test (Giữ nguyên và đồng bộ BOM) ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.00")) // Đồng bộ BOM
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}