package com.example.salesapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.salesapp.ui.components.FilterSortBottomSheet
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
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // FILTER & SORT BAR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current filter/sort info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when {
                                uiState.filterState.selectedCategoryId != null -> {
                                    val category = uiState.categories.find {
                                        it.categoryID == uiState.filterState.selectedCategoryId
                                    }
                                    "Danh mục: ${category?.categoryName ?: "N/A"}"
                                }
                                else -> "Tất cả sản phẩm"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.filterState.selectedSortOption.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    // Filter button
                    FilledTonalButton(
                        onClick = { showFilterSheet = true },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lọc")

                        // Badge indicator nếu có active filters
                        if (viewModel.hasActiveFilters()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge {
                                Text(
                                    "1",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            // CATEGORY CHIPS (Quick filter)
            if (uiState.categories.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "Tất cả" chip
                        item {
                            FilterChip(
                                selected = uiState.filterState.selectedCategoryId == null,
                                onClick = { viewModel.filterByCategory(null) },
                                label = { Text("Tất cả") }
                            )
                        }

                        // Category chips
                        items(uiState.categories) { category ->
                            FilterChip(
                                selected = uiState.filterState.selectedCategoryId == category.categoryID,
                                onClick = { viewModel.filterByCategory(category.categoryID) },
                                label = { Text(category.categoryName) }
                            )
                        }
                    }
                }
            }

            // PRODUCTS GRID
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxHeight(0.5f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.errorMessage != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchProducts() }) {
                            Text("Thử lại")
                        }
                    }
                }
            } else if (uiState.products.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxHeight(0.5f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Không tìm thấy sản phẩm",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.clearFilters() }) {
                                Text("Xóa bộ lọc")
                            }
                        }
                    }
                }
            } else {
                val productCount = uiState.products.size
                val gridHeight = ((productCount / 2) + (productCount % 2)) * 280

                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .height(gridHeight.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
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

    // Filter/Sort Bottom Sheet
    if (showFilterSheet) {
        FilterSortBottomSheet(
            categories = uiState.categories,
            selectedCategoryId = uiState.filterState.selectedCategoryId,
            selectedSortOption = uiState.filterState.selectedSortOption,
            currentMinPrice = uiState.filterState.minPrice,
            currentMaxPrice = uiState.filterState.maxPrice,
            onCategorySelected = { categoryId ->
                viewModel.filterByCategory(categoryId)
            },
            onSortSelected = { sortOption ->
                viewModel.sortBy(sortOption)
            },
            onPriceRangeApplied = { minPrice, maxPrice ->
                viewModel.filterByPriceRange(minPrice, maxPrice)
            },
            onClearFilters = {
                viewModel.clearFilters()
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}