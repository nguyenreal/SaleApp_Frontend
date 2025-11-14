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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.salesapp.ui.screens.MainScreen
import com.example.salesapp.ui.screens.cart.CartScreen
import com.example.salesapp.ui.screens.conversation.ConversationScreen
import com.example.salesapp.ui.screens.login.LoginScreen
import com.example.salesapp.ui.screens.register.RegisterScreen
import com.example.salesapp.ui.theme.SalesAppTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.salesapp.ui.screens.product.ProductDetailScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PRODUCT_DETAIL = "product_detail"
    const val CONVERSATION = "conversation"
    const val CART = "cart"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Xử lý kết quả (tùy chọn)
        if (permissions[Manifest.permission.POST_NOTIFICATIONS] == true) {
            // Quyền thông báo OK
        } else {
            // Quyền thông báo bị từ chối
            Toast.makeText(this, "Bạn sẽ không nhận được thông báo", Toast.LENGTH_SHORT).show()
        }

        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            // Quyền vị trí OK
        } else {
            // Quyền vị trí bị từ chối
            Toast.makeText(this, "Không thể hiển thị vị trí của bạn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askAppPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Quyền Thông báo (cho Badge)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // Chỉ hỏi nếu có quyền chưa được cấp
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "cart_channel"
            val name = "Giỏ hàng"
            val descriptionText = "Thông báo số lượng giỏ hàng"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        askAppPermissions()
        createNotificationChannel()

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
    val context = LocalContext.current

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
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(route = Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Routes.HOME) {
            MainScreen(
                onProductClick = { productId: Int ->
                    navController.navigate("${Routes.PRODUCT_DETAIL}/$productId")
                },
                onCartClick = {
                    navController.navigate(Routes.CART)
                },
                onChatUserClick = { userId, username ->
                    navController.navigate("${Routes.CONVERSATION}/$userId?username=$username")
                }
            )
        }

        composable(
            route = "${Routes.PRODUCT_DETAIL}/{productId}",
        ) {
            ProductDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Routes.CART)
                }
            )
        }

        composable(route = Routes.CART) {
            CartScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            // Route sẽ có dạng: "conversation/5?username=Admin"
            route = "${Routes.CONVERSATION}/{userId}?username={username}",
        ) { backStackEntry ->
            // Lấy tham số
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            val username = backStackEntry.arguments?.getString("username")

            if (userId != null && username != null) {
                ConversationScreen(
                    otherUserId = userId,
                    otherUsername = username,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                // Xử lý lỗi (ví dụ: quay lại)
                navController.popBackStack()
            }
        }
    }
}