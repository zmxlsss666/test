package com.tomatoreader.wear.presentation.components.offline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.icons.Icons as WearIcons
import androidx.wear.compose.material.icons.filled.CloudDownload
import androidx.wear.compose.material.icons.filled.CloudDone
import androidx.wear.compose.material.icons.filled.Error
import com.tomatoreader.wear.presentation.theme.Shapes

@Composable
fun OfflineStatusIndicator(
    isAvailableOffline: Boolean,
    isDownloading: Boolean,
    downloadProgress: Float,
    downloadError: String?,
    onDownloadClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(WearMaterialTheme.colors.surface)
            .clickable {
                when {
                    isAvailableOffline -> onRemoveClick()
                    !isAvailableOffline && !isDownloading -> onDownloadClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            downloadError != null -> {
                Icon(
                    imageVector = WearIcons.Default.Error,
                    contentDescription = "Download error",
                    tint = WearMaterialTheme.colors.error,
                    modifier = Modifier.size(16.dp)
                )
            }
            isDownloading -> {
                CircularProgressIndicator(
                    progress = downloadProgress,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    colors = CircularProgressIndicatorDefaults.colors(
                        indicatorColor = WearMaterialTheme.colors.primary
                    )
                )
            }
            isAvailableOffline -> {
                Icon(
                    imageVector = WearIcons.Default.CloudDone,
                    contentDescription = "Available offline",
                    tint = WearMaterialTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = WearIcons.Default.CloudDownload,
                    contentDescription = "Download for offline reading",
                    tint = WearMaterialTheme.colors.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun OfflineStatusIndicatorWithText(
    isAvailableOffline: Boolean,
    isDownloading: Boolean,
    downloadProgress: Float,
    downloadError: String?,
    onDownloadClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        OfflineStatusIndicator(
            isAvailableOffline = isAvailableOffline,
            isDownloading = isDownloading,
            downloadProgress = downloadProgress,
            downloadError = downloadError,
            onDownloadClick = onDownloadClick,
            onRemoveClick = onRemoveClick
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        when {
            downloadError != null -> {
                Text(
                    text = "Error",
                    color = WearMaterialTheme.colors.error,
                    style = WearMaterialTheme.typography.caption1.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            isDownloading -> {
                Text(
                    text = "${(downloadProgress * 100).toInt()}%",
                    color = WearMaterialTheme.colors.primary,
                    style = WearMaterialTheme.typography.caption1.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            isAvailableOffline -> {
                Text(
                    text = "Offline",
                    color = WearMaterialTheme.colors.primary,
                    style = WearMaterialTheme.typography.caption1.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            else -> {
                Text(
                    text = "Download",
                    color = WearMaterialTheme.colors.onSurface,
                    style = WearMaterialTheme.typography.caption1
                )
            }
        }
    }
}