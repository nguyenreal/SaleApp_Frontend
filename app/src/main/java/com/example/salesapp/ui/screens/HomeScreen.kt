package com.example.salesapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.salesapp.ui.components.ProductCard
import com.example.salesapp.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit
) {
    val uiState = viewModel.uiState

    Scaffold(
        // THANH BAR TRÊN CÙNG
        topBar = {
            TopAppBar(
                title = {
                    // THANH TÌM KIẾM (GIẢ)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { /* TODO: Navigate to Search Screen */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Tìm kiếm sản phẩm...", color = Color.Gray)
                        }
                    }
                },
                actions = {
                    // NÚT GIỎ HÀNG
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Giỏ hàng"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        // --- NỘI DUNG CHÍNH (SCROLL) ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Rất quan trọng
            contentPadding = PaddingValues(bottom = 80.dp) // Tránh bị che bởi Bottom Nav
        ) {

            // 1. BANNER QUẢNG CÁO (PLACEHOLDER)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Banner Quảng Cáo", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            // 2. DANH MỤC (PLACEHOLDER)
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Danh Mục",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(5) {
                            Card(
                                modifier = Modifier.size(80.dp),
                                onClick = { /* TODO: Lọc theo danh mục */ }
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Laptop", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            // 3. TIÊU ĐỀ "GỢI Ý HÔM NAY"
            item {
                Text(
                    text = "Gợi ý hôm nay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 4. LƯỚI SẢN PHẨM (NỘI DUNG CHÍNH)
            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier
                        .fillParentMaxHeight(0.5f) // Chiếm 50% chiều cao còn lại
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.errorMessage != null) {
                item {
                    Text(uiState.errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            } else {
                // Lồng một LazyVerticalGrid bên trong LazyColumn
                // Cần set chiều cao cố định

                // <<< SỬA LỖI 1 TẠI ĐÂY: Dùng "uiState.products" >>>
                val productCount = uiState.products.size
                val gridHeight = ((productCount / 2) + (productCount % 2)) * 280 // Ước tính chiều cao

                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .height(gridHeight.dp) // <-- Cần thiết
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),

                        // <<< SỬA LỖI 2 & 3 TẠI ĐÂY: Dùng "userScrollEnabled" >>>
                        userScrollEnabled = false // <-- Rất quan trọng
                    ) {
                        items(uiState.products) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.productID) }
                            )
                        }
                    }
                }
            }
        }
    }
}