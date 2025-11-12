// Vị trí: .../ui/components/ProductCard.kt
package com.example.salesapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.salesapp.data.remote.dto.ProductListItemDto
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: ProductListItemDto,
    onClick: () -> Unit
) {
    // Định dạng giá tiền Việt Nam
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            AsyncImage(
                model = product.imageURL, // Coil sẽ tải ảnh từ URL
                contentDescription = product.productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Tạo ảnh vuông
                contentScale = ContentScale.Crop // Cắt ảnh cho vừa
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(40.dp) // Giữ chiều cao cố định
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatter.format(product.price),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}