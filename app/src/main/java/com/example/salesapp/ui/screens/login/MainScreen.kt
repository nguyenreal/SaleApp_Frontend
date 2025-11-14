// Vị trí: .../ui/screens/MainScreen.kt
package com.example.salesapp.ui.screens
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.salesapp.ui.screens.home.HomeScreen

// Định nghĩa 3 tab chính của app
sealed class MainAppRoutes(val route: String, val label: String, val icon: ImageVector) {
    object Home : MainAppRoutes("home_tab", "Trang chủ", Icons.Default.Home)
    // (object Home và object Profile giữ nguyên)
    object Chat : MainAppRoutes("chat_tab", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Profile : MainAppRoutes("profile_tab", "Cá nhân", Icons.Default.Person)
}

val mainAppTabs = listOf(
    MainAppRoutes.Home,
    MainAppRoutes.Chat,
    MainAppRoutes.Profile
)

@Composable
fun MainScreen(
    onProductClick: (Int) -> Unit, // <-- Phải là (Int) -> Unit
    onCartClick: () -> Unit
) {
    val mainNavController = rememberNavController() // Bộ điều khiển nav cho 3 tab

    Scaffold(
        // THANH ĐIỀU HƯỚNG DƯỚI
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                mainAppTabs.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            mainNavController.navigate(screen.route) {
                                popUpTo(mainNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // NỘI DUNG CHÍNH CỦA APP (chuyển đổi giữa các tab)
        NavHost(
            navController = mainNavController,
            startDestination = MainAppRoutes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Tab 1: Trang Chủ
            composable(MainAppRoutes.Home.route) {
                HomeScreen(
                    onProductClick = onProductClick,
                    onCartClick = onCartClick
                )
            }

            // Tab 2: Chat (Placeholder)
            composable(MainAppRoutes.Chat.route) {
                // (Chúng ta sẽ implement màn hình Chat ở đây)
                Text("Màn hình Chat")
            }

            // Tab 3: Cá nhân (Placeholder)
            composable(MainAppRoutes.Profile.route) {
                // (Chúng ta sẽ implement màn hình Profile ở đây)
                Text("Màn hình Cá nhân (Logout,...)")
            }
        }
    }
}