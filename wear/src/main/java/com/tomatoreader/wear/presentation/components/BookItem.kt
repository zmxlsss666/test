package com.tomatoreader.wear.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.icons.Icons as WearIcons
import com.tomatoreader.core.data.BookEntity
import com.tomatoreader.wear.presentation.theme.Shapes

@Composable
fun BookItem(
    book: BookEntity,
    onClick: () -> Unit,
    offlineStatusIndicator: @Composable () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = WearMaterialTheme.colors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(WearMaterialTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = WearIcons.Default.Book,
                    contentDescription = null,
                    tint = WearMaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Book info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = WearMaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = WearMaterialTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = book.author,
                    style = WearMaterialTheme.typography.body2,
                    color = WearMaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (book.readingProgress > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "进度: ${(book.readingProgress * 100).toInt()}%",
                        style = WearMaterialTheme.typography.caption2,
                        color = WearMaterialTheme.colors.primary
                    )
                }
            }
            
            // Offline status indicator
            offlineStatusIndicator()
        }
    }
}