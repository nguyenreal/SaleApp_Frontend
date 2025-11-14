package com.example.salesapp.ui.screens.product

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.salesapp.viewmodel.ProductDetailViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    // <<< SỬA LỖI TẠI ĐÂY: Thêm tham số onNavigateToCart >>>
    onNavigateToCart: () -> Unit
) {
    val uiState = viewModel.uiState
    val product = uiState.product
    val context = LocalContext.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    LaunchedEffect(uiState.addToCartSuccess, uiState.addToCartError) {
        if (uiState.addToCartSuccess) {
            Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
            viewModel.clearAddToCartStatus()

            // <<< SỬ DỤNG THAM SỐ MỚI >>>
            // Tự động chuyển đến giỏ hàng sau khi thêm thành công
            onNavigateToCart()
        }
        if (uiState.addToCartError != null) {
            Toast.makeText(context, "Lỗi: ${uiState.addToCartError}", Toast.LENGTH_LONG).show()
            viewModel.clearAddToCartStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.productName ?: "Chi tiết sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            if (product != null) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = { viewModel.addToCart(product.productID, 1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        enabled = !uiState.isAddingToCart
                    ) {
                        if (uiState.isAddingToCart) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Thêm vào giỏ hàng", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            }
            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
            product == null -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Không tìm thấy sản phẩm.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        AsyncImage(
                            model = product.imageURL,
                            contentDescription = product.productName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    }
                    item {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = product.productName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = formatter.format(product.price),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Mô tả",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                // Lỗi "Unresolved reference 'description'" (image_b44be7.png) sẽ hết
                                // sau khi bạn sửa file ProductDetailDto.kt
                                text = product.description ?: "Sản phẩm chưa có mô tả.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}