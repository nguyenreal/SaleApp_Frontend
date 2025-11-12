pluginManagement {
    repositories {
        // <<< THÊM DÒNG NÀY: Chỉ tới kho plugin của Google
        google()
        // <<< THÊM DÒNG NÀY: Chỉ tới kho Maven Central (phổ biến)
        mavenCentral()
        // (Đây là kho mặc định, giữ lại)
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SalesApp"
include(":app")