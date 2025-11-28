package com.tomatoreader.wear.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.icons.Icons as WearIcons
import com.tomatoreader.core.data.BookmarkEntity
import com.tomatoreader.wear.presentation.theme.Shapes

@Composable
fun BookmarkItem(
    bookmark: BookmarkEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 书签信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bookmark.chapterTitle,
                    style = WearMaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = WearMaterialTheme.colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (bookmark.note.isNotEmpty()) {
                    Text(
                        text = bookmark.note,
                        style = WearMaterialTheme.typography.caption2,
                        color = WearMaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                }
                
                Text(
                    text = "位置: ${bookmark.position}",
                    style = WearMaterialTheme.typography.caption2,
                    color = WearMaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 删除按钮
            Button(
                onClick = onDelete,
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