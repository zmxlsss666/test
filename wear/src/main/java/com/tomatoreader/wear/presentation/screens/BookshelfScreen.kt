package com.tomatoreader.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import com.tomatoreader.wear.R
import com.tomatoreader.wear.presentation.components.BookItem
import com.tomatoreader.wear.presentation.components.RotaryAwareScalingLazyColumn
import com.tomatoreader.wear.presentation.components.offline.OfflineStatusIndicator
import com.tomatoreader.wear.presentation.viewmodel.BookshelfViewModel
import com.tomatoreader.wear.presentation.theme.Shapes
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BookshelfScreen(
    onBookClick: (String) -> Unit,
    onBookmarkClick: () -> Unit,
    onSearchClick: () -> Unit,
    onOfflineClick: () -> Unit,
    viewModel: BookshelfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadBooks()
    }
    
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.Top) }
    ) {
        if (uiState.isLoading) {
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
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.error_occurred),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.loadBooks() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.retry),
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        } else if (uiState.books.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_books_found),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        } else {
            RotaryAwareScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
                onRotaryScroll = { delta ->
                    // Handle rotary scroll if needed
                }
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            backgroundColor = MaterialTheme.colors.primaryContainer
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.bookshelf),
                            style = MaterialTheme.typography.title2,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                items(uiState.books) { book ->
                    BookItem(
                        book = com.tomatoreader.core.data.BookEntity(
                            id = book.id,
                            title = book.title,
                            author = book.author,
                            coverUrl = book.coverUrl,
                            lastReadTime = book.lastReadTime,
                            lastReadChapter = book.lastReadChapter,
                            readingProgress = book.readingProgress
                        ),
                        onClick = { onBookClick(book.id) },
                        offlineStatusIndicator = {
                            OfflineStatusIndicator(
                                isAvailableOffline = book.isAvailableOffline,
                                isDownloading = book.isDownloading,
                                downloadProgress = book.downloadProgress,
                                downloadError = book.downloadError,
                                onDownloadClick = { 
                                    if (!book.isAvailableOffline && !book.isDownloading) {
                                        viewModel.downloadBookForOffline(book.id)
                                    }
                                },
                                onRemoveClick = { 
                                    if (book.isAvailableOffline) {
                                        viewModel.removeOfflineBook(book.id)
                                    }
                                }
                            )
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            backgroundColor = MaterialTheme.colors.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = onBookmarkClick,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondary
                                ),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = stringResource(R.string.bookmarks),
                                    tint = MaterialTheme.colors.onSecondary
                                )
                            }
                            
                            Button(
                                onClick = onOfflineClick,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondary
                                ),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = "离线管理",
                                    tint = MaterialTheme.colors.onSecondary
                                )
                            }
                            
                            Button(
                                onClick = onSearchClick,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondary
                                ),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.search),
                                    tint = MaterialTheme.colors.onSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}