package com.example.salesapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.salesapp.ui.screens.chat.ChatListScreen
import com.example.salesapp.ui.screens.home.HomeScreen
import com.example.salesapp.ui.screens.map.MapScreen
import com.example.salesapp.viewmodel.MainViewModel

// Định nghĩa 4 tab chính của app
sealed class MainAppRoutes(val route: String, val label: String, val icon: ImageVector) {
    object Home : MainAppRoutes("home_tab", "Trang chủ", Icons.Default.Home)
    object Map : MainAppRoutes("map_tab", "Cửa hàng", Icons.Default.LocationOn)
    object Chat : MainAppRoutes("chat_tab", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Profile : MainAppRoutes("profile_tab", "Cá nhân", Icons.Default.Person)
}

val mainAppTabs = listOf(
    MainAppRoutes.Home,
    MainAppRoutes.Map,
    MainAppRoutes.Chat,
    MainAppRoutes.Profile
)

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onChatUserClick: (Int, String) -> Unit
) {
    val mainNavController = rememberNavController() // Bộ điều khiển nav cho các tab

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
                        // Quan trọng: Chỉ dùng onClick, không dùng indication hay Modifier.clickable
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

            // Tab 2: Chat
            composable(MainAppRoutes.Chat.route) {
                ChatListScreen(
                    onChatUserClick = onChatUserClick // Truyền callback
                )
            }

            // Tab 3: Cửa hàng (Map)
            composable(MainAppRoutes.Map.route) {
                MapScreen()
            }

            // Tab 4: Cá nhân
            composable(MainAppRoutes.Profile.route) {
                // (Chúng ta sẽ implement màn hình Profile ở đây)
                Text("Màn hình Cá nhân (Logout,...)")
            }
        }
    }
}