package com.tomatoreader.wear.presentation.components.offline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.icons.Icons as WearIcons
import androidx.wear.compose.material.icons.filled.Download
import androidx.wear.compose.material.icons.filled.DownloadDone
import androidx.wear.compose.material.icons.filled.Error
import androidx.wear.compose.material.icons.filled.FileDownloadOff
import com.tomatoreader.wear.presentation.theme.Shapes

/**
 * 离线状态指示器组件
 * 显示书籍的离线状态和下载进度
 */
@Composable
fun OfflineStatusIndicator(
    isAvailableOffline: Boolean,
    downloadProgress: Float = 0f,
    isDownloading: Boolean = false,
    downloadError: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            downloadError != null -> {
                // 下载错误状态
                Icon(
                    imageVector = WearIcons.Default.Error,
                    contentDescription = "下载错误",
                    tint = WearMaterialTheme.colors.error,
                    modifier = Modifier.size(24.dp)
                )
            }
            isDownloading -> {
                // 下载中状态
                CircularProgressIndicator(
                    progress = downloadProgress,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp,
                    colors = CircularProgressIndicatorDefaults.colors(
                        indicatorColor = WearMaterialTheme.colors.primary
                    )
                )
            }
            isAvailableOffline -> {
                // 已下载状态
                Icon(
                    imageVector = WearIcons.Default.DownloadDone,
                    contentDescription = "已下载",
                    tint = WearMaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            else -> {
                // 未下载状态
                Icon(
                    imageVector = WearIcons.Default.Download,
                    contentDescription = "未下载",
                    tint = WearMaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * 离线下载按钮组件
 */
@Composable
fun OfflineDownloadButton(
    isAvailableOffline: Boolean,
    isDownloading: Boolean = false,
    downloadProgress: Float = 0f,
    downloadError: String? = null,
    onDownloadClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            if (isAvailableOffline) {
                onRemoveClick()
            } else {
                onDownloadClick()
            }
        },
        modifier = modifier,
        shape = Shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isDownloading) {
                CircularProgressIndicator(
                    progress = downloadProgress,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    colors = CircularProgressIndicatorDefaults.colors(
                        indicatorColor = WearMaterialTheme.colors.primary
                    )
                )
            } else {
                Icon(
                    imageVector = if (isAvailableOffline) {
                        WearIcons.Default.FileDownloadOff
                    } else {
                        WearIcons.Default.Download
                    },
                    contentDescription = if (isAvailableOffline) {
                        "删除离线内容"
                    } else {
                        "下载离线内容"
                    },
                    tint = WearMaterialTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = when {
                    downloadError != null -> "错误"
                    isDownloading -> "${(downloadProgress * 100).toInt()}%"
                    isAvailableOffline -> "删除"
                    else -> "下载"
                },
                style = WearMaterialTheme.typography.button,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 离线书籍卡片组件
 */
@Composable
fun OfflineBookCard(
    bookTitle: String,
    author: String,
    isAvailableOffline: Boolean,
    isDownloading: Boolean = false,
    downloadProgress: Float = 0f,
    downloadError: String? = null,
    onDownloadClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* 卡片点击事件，可以用于导航到书籍详情 */ },
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = bookTitle,
                style = WearMaterialTheme.typography.title3,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = author,
                style = WearMaterialTheme.typography.caption1,
                color = WearMaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 状态文本
            Text(
                text = when {
                    downloadError != null -> "下载失败"
                    isDownloading -> "下载中..."
                    isAvailableOffline -> "已下载"
                    else -> "未下载"
                },
                style = WearMaterialTheme.typography.caption2,
                color = when {
                    downloadError != null -> WearMaterialTheme.colors.error
                    isDownloading -> WearMaterialTheme.colors.primary
                    isAvailableOffline -> WearMaterialTheme.colors.primary
                    else -> WearMaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            OfflineDownloadButton(
                isAvailableOffline = isAvailableOffline,
                isDownloading = isDownloading,
                downloadProgress = downloadProgress,
                downloadError = downloadError,
                onDownloadClick = onDownloadClick,
                onRemoveClick = onRemoveClick
            )
        }
    }
}

/**
 * 离线阅读状态文本组件
 */
@Composable
fun OfflineStatusText(
    isAvailableOffline: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (isAvailableOffline) "可离线阅读" else "仅在线阅读",
        style = MaterialTheme.typography.caption2,
        color = if (isAvailableOffline) 
            MaterialTheme.colors.primary 
        else 
            MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}