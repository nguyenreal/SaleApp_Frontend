// Vị trí: .../ui/screens/cart/CartScreen.kt
package com.example.salesapp.ui.screens.cart

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.salesapp.data.remote.dto.CartItemDto
import com.example.salesapp.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val cart = uiState.cart
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ Hàng Của Bạn") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        // Thanh "Tổng tiền" và "Thanh Toán" ở dưới
        bottomBar = {
            if (cart != null && cart.items.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Tổng cộng:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = formatter.format(cart.totalPrice),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = { /* TODO: Navigate to Checkout */ },
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Thanh Toán", fontSize = 16.sp)
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
            cart == null || cart.items.isEmpty() -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Giỏ hàng của bạn đang trống.")
                }
            }
            // Hiển thị danh sách sản phẩm
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cart.items) { item ->
                        CartItemCard(
                            item = item,
                            formatter = formatter,
                            onIncrease = { viewModel.updateQuantity(item.cartItemID, item.quantity + 1) },
                            onDecrease = { viewModel.updateQuantity(item.cartItemID, item.quantity - 1) },
                            onRemove = { viewModel.removeItem(item.cartItemID) }
                        )
                    }
                }
            }
        }
    }
}

// Component Card cho từng item trong giỏ
@Composable
fun CartItemCard(
    item: CartItemDto,
    formatter: NumberFormat,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = item.imageURL, // <-- Dùng URL thật
                contentDescription = item.productName,
                modifier = Modifier
                    .size(100.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f) // Chiếm hết không gian còn lại
                    .height(100.dp), // Cùng chiều cao với ảnh
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Tên và Nút Xóa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.productName ?: "Sản phẩm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Gray)
                    }
                }

                // Giá và Bộ đếm số lượng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = formatter.format(item.price),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    // Bộ đếm
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        IconButton(onClick = onDecrease, enabled = item.quantity > 1) {
                            Icon(Icons.Default.Remove, "Giảm")
                        }
                        Text(item.quantity.toString(), fontWeight = FontWeight.Bold)
                        IconButton(onClick = onIncrease) {
                            Icon(Icons.Default.Add, "Tăng")
                        }
                    }
                }
            }
        }
    }
}