package com.tomatoreader.wear.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CircularProgressIndicatorDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.icons.Icons
import androidx.wear.compose.material.icons.filled.ArrowBack
import androidx.wear.compose.material.icons.filled.DownloadDone
import androidx.wear.compose.material.icons.filled.Storage
import androidx.wear.compose.material.icons.filled.Warning
import com.tomatoreader.wear.R
import com.tomatoreader.wear.presentation.components.RotaryAwareScalingLazyColumn
import com.tomatoreader.wear.presentation.components.offline.OfflineBookCard
import com.tomatoreader.wear.presentation.components.offline.StorageInfoCard
import com.tomatoreader.wear.presentation.viewmodel.OfflineViewModel
import com.tomatoreader.wear.presentation.theme.Shapes

/**
 * 离线管理界面
 * 显示已下载的书籍列表和管理选项
 */
@Composable
fun OfflineScreen(
    onBackClick: () -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OfflineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadOfflineBooks()
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部标题栏
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    backgroundColor = MaterialTheme.colors.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "离线阅读",
                        style = MaterialTheme.typography.title2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // 存储信息
            StorageInfoCard(
                totalSize = uiState.totalStorageSize,
                usedSize = uiState.usedStorageSize,
                bookCount = uiState.offlineBooks.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 书籍列表
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            colors = CircularProgressIndicatorDefaults.colors(
                                indicatorColor = MaterialTheme.colors.primary
                            )
                        )
                    }
                }
                
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                backgroundColor = MaterialTheme.colors.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "错误",
                                    tint = MaterialTheme.colors.error,
                                    modifier = Modifier.size(32.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = uiState.errorMessage,
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Button(
                                    onClick = { viewModel.loadOfflineBooks() },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.primary
                                    )
                                ) {
                                    Text(
                                        text = "重试",
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
                
                uiState.offlineBooks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                backgroundColor = MaterialTheme.colors.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "无离线内容",
                                    tint = MaterialTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "暂无离线书籍",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                else -> {
                    RotaryAwareScalingLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        onRotaryScroll = { scrollDelta ->
                            // 处理旋转表圈滚动
                        }
                    ) {
                        items(uiState.offlineBooks) { book ->
                            OfflineBookCard(
                                bookTitle = book.title,
                                author = book.author,
                                isAvailableOffline = true,
                                onDownloadClick = { /* 不应该触发，因为已经是离线状态 */ },
                                onRemoveClick = { viewModel.removeOfflineBook(book.bookId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // 点击卡片导航到阅读界面
                                onBookClick(book.bookId)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 存储信息卡片组件
 */
@Composable
private fun StorageInfoCard(
    totalSize: String,
    usedSize: String,
    bookCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            backgroundColor = MaterialTheme.colors.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "存储信息",
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "存储",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "已用空间",
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                
                Text(
                    text = usedSize,
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "总空间",
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                
                Text(
                    text = totalSize,
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "离线书籍",
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                
                Text(
                    text = "$bookCount 本",
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}