// Vị trí: .../com/example/salesapp/MainActivity.kt
package com.example.salesapp

import android.os.Bundle
import android.widget.Toast // <-- Thêm
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // <-- Thêm
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.salesapp.ui.screens.login.LoginScreen
import com.example.salesapp.ui.screens.register.RegisterScreen // <-- Thêm
import com.example.salesapp.ui.theme.SalesAppTheme
import dagger.hilt.android.AndroidEntryPoint

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register" // <-- Thêm
    const val HOME = "home"
    const val PRODUCT_DETAIL = "product_detail"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SalesAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current // Để hiển thị Toast

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(route = Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER) // Chuyển sang Đăng ký
                }
            )
        }

        // --- THÊM ROUTE NÀY ---
        composable(route = Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    // <<< DÒNG NÀY ĐANG HIỂN THỊ THÔNG BÁO >>>
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack() // Quay lại màn hình Login
                }
            )
        }

        composable(route = Routes.HOME) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Text(text = "Đã Đăng Nhập Thành Công! Chào mừng tới Home.")
            }
        }
    }
}