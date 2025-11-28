package com.tomatoreader.wear.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.icons.Icons as WearIcons
import com.tomatoreader.core.data.BookEntity
import com.tomatoreader.wear.presentation.theme.Shapes

@Composable
fun BookItem(
    book: com.tomatoreader.core.data.BookEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    offlineStatusIndicator: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = WearMaterialTheme.colors.surface
        ),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = book.title,
                        style = WearMaterialTheme.typography.title3,
                        fontWeight = FontWeight.Bold,
                        color = WearMaterialTheme.colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = book.author,
                        style = WearMaterialTheme.typography.caption1,
                        color = WearMaterialTheme.colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // 离线状态指示器
                offlineStatusIndicator?.invoke()
            }
            
            if (book.lastReadTime > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "最近阅读: ${formatLastReadTime(book.lastReadTime)}",
                    style = WearMaterialTheme.typography.caption2,
                    color = WearMaterialTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ChapterItem(
    chapter: com.tomatoreader.core.data.ChapterEntity,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) 
                WearMaterialTheme.colors.primaryContainer 
            else 
                WearMaterialTheme.colors.surface
        ),
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.title,
                    style = if (isCurrent) 
                        WearMaterialTheme.typography.title3 
                    else 
                        WearMaterialTheme.typography.body1,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) 
                        WearMaterialTheme.colors.onPrimaryContainer 
                    else 
                        WearMaterialTheme.colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (isCurrent) {
                Icon(
                    imageVector = WearIcons.Default.DownloadDone,
                    contentDescription = "当前章节",
                    tint = WearMaterialTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BookmarkItem(
    bookmark: com.tomatoreader.core.data.BookmarkEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = WearMaterialTheme.colors.surfaceVariant
        ),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bookmark.chapterTitle,
                        style = WearMaterialTheme.typography.title3,
                        fontWeight = FontWeight.Medium,
                        color = WearMaterialTheme.colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = bookmark.note.ifBlank { "无备注" },
                        style = WearMaterialTheme.typography.body2,
                        color = WearMaterialTheme.colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "位置: ${bookmark.position}",
                        style = WearMaterialTheme.typography.caption2,
                        color = WearMaterialTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WearMaterialTheme.colors.error
                    ),
                    modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                    shape = Shapes.small
                ) {
                    Icon(
                        imageVector = WearIcons.Default.Delete,
                        contentDescription = "删除书签",
                        tint = WearMaterialTheme.colors.onError
                    )
                }
            }
        }
    }
}

private fun formatLastReadTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60 * 1000 -> "刚刚"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
        diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}天前"
        else -> "更早"
    }
}