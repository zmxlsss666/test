package com.tomatoreader.wear.presentation.components.offline

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.icons.Icons as WearIcons
import androidx.wear.compose.material.icons.filled.Delete
import com.tomatoreader.wear.presentation.theme.Shapes

@Composable
fun OfflineBookCard(
    bookTitle: String,
    author: String,
    isAvailableOffline: Boolean,
    onDownloadClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WearMaterialTheme.colors.surface
        ),
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 书籍信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bookTitle,
                    style = WearMaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = WearMaterialTheme.colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = author,
                    style = WearMaterialTheme.typography.caption1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = WearMaterialTheme.colors.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 离线状态指示器
            OfflineStatusIndicator(
                isAvailableOffline = isAvailableOffline,
                isDownloading = false,
                downloadProgress = 0f,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 删除按钮
            Button(
                onClick = onRemoveClick,
                modifier = Modifier.size(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WearMaterialTheme.colors.error
                ),
                shape = Shapes.small
            ) {
                Icon(
                    imageVector = WearIcons.Default.Delete,
                    contentDescription = "删除",
                    tint = WearMaterialTheme.colors.onError,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}