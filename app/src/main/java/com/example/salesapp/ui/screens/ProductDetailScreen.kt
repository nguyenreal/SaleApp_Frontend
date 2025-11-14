// Vị trí: .../ui/screens/detail/ProductDetailScreen.kt
package com.example.salesapp.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.salesapp.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val uiState = viewModel.uiState
    val product = uiState.product
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is ProductDetailViewModel.UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Xem Giỏ Hàng", // <-- THÊM NÚT
                        duration = SnackbarDuration.Short
                    )
                    // Nếu người dùng nhấn "Xem Giỏ Hàng"
                    if (result == SnackbarResult.ActionPerformed) {
                        onNavigateToCart()
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sản phẩm") },
                navigationIcon = {
                    // Nút Back
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        // Nút "Thêm vào giỏ hàng" ở đáy màn hình
        bottomBar = {
            if (product != null) {
                Button(
                    onClick = { viewModel.addToCart() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm vào giỏ hàng", fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->

        when {
            // Đang tải...
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Lỗi...
            uiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }

            // Thành công...
            product != null -> {
                // Dùng LazyColumn để có thể cuộn
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(bottom = 80.dp) // Tránh bị nút BottomBar che
                ) {
                    // 1. Ảnh sản phẩm
                    item {
                        AsyncImage(
                            model = product.imageURL,
                            contentDescription = product.productName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f), // Ảnh vuông
                            contentScale = ContentScale.Crop
                        )
                    }

                    // 2. Tên và Giá
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = product.productName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formatter.format(product.price),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item { Divider() }

                    // 3. Mô tả chi tiết
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Mô tả chi tiết",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.fullDescription ?: "Không có mô tả.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    item { Divider() }

                    // 4. Thông số kỹ thuật
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Thông số kỹ thuật",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.technicalSpecifications ?: "Không có thông số.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}