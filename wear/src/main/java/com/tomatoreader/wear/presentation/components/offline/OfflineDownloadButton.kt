package com.tomatoreader.wear.presentation.components.offline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.icons.Icons as WearIcons
import androidx.wear.compose.material.icons.filled.CloudDownload
import androidx.wear.compose.material.icons.filled.CloudDone
import androidx.wear.compose.material.icons.filled.CloudOff
import androidx.wear.compose.material.icons.filled.Delete
import androidx.wear.compose.material.icons.filled.Error

@Composable
fun OfflineDownloadButton(
    isAvailableOffline: Boolean,
    isDownloading: Boolean,
    downloadProgress: Float,
    downloadError: String?,
    onDownloadClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(WearMaterialTheme.colors.primary)
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
                    tint = WearMaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            isDownloading -> {
                CircularProgressIndicator(
                    progress = downloadProgress,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    strokeCap = StrokeCap.Round,
                    colors = CircularProgressIndicatorDefaults.colors(
                        indicatorColor = WearMaterialTheme.colors.onPrimary
                    )
                )
            }
            isAvailableOffline -> {
                Icon(
                    imageVector = WearIcons.Default.CloudDone,
                    contentDescription = "Available offline",
                    tint = WearMaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = WearIcons.Default.CloudDownload,
                    contentDescription = "Download for offline reading",
                    tint = WearMaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}