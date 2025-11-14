package com.example.salesapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.salesapp.data.remote.dto.CategoryDto
import com.example.salesapp.viewmodel.SortOption
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortBottomSheet(
    categories: List<CategoryDto>,
    selectedCategoryId: Int?,
    selectedSortOption: SortOption,
    currentMinPrice: Double?,
    currentMaxPrice: Double?,
    onCategorySelected: (Int?) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onPriceRangeApplied: (Double?, Double?) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    var minPriceText by remember { mutableStateOf(currentMinPrice?.toString() ?: "") }
    var maxPriceText by remember { mutableStateOf(currentMaxPrice?.toString() ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lọc & Sắp xếp",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    onClearFilters()
                    minPriceText = ""
                    maxPriceText = ""
                }) {
                    Text("Xóa tất cả")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. SORT OPTIONS
            Text(
                text = "Sắp xếp theo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            SortOption.values().forEach { option ->
                FilterChip(
                    selected = selectedSortOption == option,
                    onClick = { onSortSelected(option) },
                    label = { Text(option.displayName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. CATEGORY FILTER
            Text(
                text = "Danh mục",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // "Tất cả" chip
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("Tất cả") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.categoryID,
                        onClick = { onCategorySelected(category.categoryID) },
                        label = { Text("${category.categoryName} (${category.productCount})") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. PRICE RANGE FILTER
            Text(
                text = "Khoảng giá",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minPriceText,
                    onValueChange = { minPriceText = it.filter { char -> char.isDigit() } },
                    label = { Text("Giá tối thiểu") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = maxPriceText,
                    onValueChange = { maxPriceText = it.filter { char -> char.isDigit() } },
                    label = { Text("Giá tối đa") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // APPLY BUTTON
            Button(
                onClick = {
                    val minPrice = minPriceText.toDoubleOrNull()
                    val maxPrice = maxPriceText.toDoubleOrNull()
                    onPriceRangeApplied(minPrice, maxPrice)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Áp dụng")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}