package com.tomatoreader.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.tomatoreader.wear.presentation.components.ChapterItem
import com.tomatoreader.wear.presentation.components.RotaryAwareReaderContent
import com.tomatoreader.wear.presentation.components.offline.OfflineDownloadButton
import com.tomatoreader.wear.presentation.viewmodel.ReaderViewModel
import com.tomatoreader.wear.presentation.theme.Shapes
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ReaderScreen(
    bookId: String,
    onBack: () -> Unit,
    onBookmarksClick: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }
    
    LaunchedEffect(uiState.currentChapter) {
        // Scroll to current chapter when it changes
        uiState.currentChapter?.let { chapter ->
            val index = uiState.chapters.indexOfFirst { it.id == chapter.id }
            if (index >= 0) {
                listState.animateScrollToItem(index)
            }
        }
    }
    
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.Top) },
        positionIndicator = {
            PositionIndicator(
                state = listState,
                modifier = Modifier
            )
        }
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
                        onClick = { viewModel.loadBook(bookId) },
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
        } else if (uiState.chapters.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_books_found),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            }
        } else {
            RotaryAwareReaderContent(
                modifier = Modifier.fillMaxSize()
            ) { onRotaryScroll ->
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 40.dp
                        )
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(
                                    backgroundColor = MaterialTheme.colors.primaryContainer
                                ),
                                shape = Shapes.medium
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = onBack,
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = MaterialTheme.colors.secondary
                                        ),
                                        modifier = Modifier.size(32.dp),
                                        shape = Shapes.small
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back",
                                            tint = MaterialTheme.colors.onSecondary
                                        )
                                    }
                                    
                                    Text(
                                        text = uiState.book?.title ?: "",
                                        style = MaterialTheme.typography.title3,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colors.onPrimaryContainer,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    // Offline download button
                                    OfflineDownloadButton(
                                        isAvailableOffline = uiState.isAvailableOffline,
                                        isDownloading = uiState.isDownloading,
                                        downloadProgress = uiState.downloadProgress,
                                        downloadError = uiState.downloadError,
                                        onDownloadClick = { viewModel.downloadBookForOffline() },
                                        onRemoveClick = { viewModel.removeOfflineBook() },
                                        modifier = Modifier.size(32.dp)
                                    )
                                    
                                    Button(
                                        onClick = onBookmarksClick,
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = MaterialTheme.colors.secondary
                                        ),
                                        modifier = Modifier.size(32.dp),
                                        shape = Shapes.small
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = if (uiState.isBookmarked) {
                                                stringResource(R.string.remove_bookmark)
                                            } else {
                                                stringResource(R.string.add_bookmark)
                                            },
                                            tint = MaterialTheme.colors.onSecondary
                                        )
                                    }
                                }
                            }
                        }
                        
                        itemsIndexed(uiState.chapters) { index, chapter ->
                            ChapterItem(
                                chapter = chapter,
                                isCurrent = chapter.id == uiState.currentChapter?.id,
                                onClick = { viewModel.loadChapter(chapter.id) }
                            )
                        }
                    }
                    
                    // Navigation buttons at bottom
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            backgroundColor = MaterialTheme.colors.surfaceVariant
                        ),
                        shape = Shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val currentIndex = uiState.chapters.indexOfFirst { it.id == uiState.currentChapter?.id }
                            
                            Button(
                                onClick = { 
                                    if (currentIndex > 0) {
                                        viewModel.loadChapter(uiState.chapters[currentIndex - 1].id)
                                    }
                                },
                                enabled = currentIndex > 0,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondary
                                ),
                                modifier = Modifier.size(40.dp),
                                shape = Shapes.small
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = stringResource(R.string.previous_chapter),
                                    tint = MaterialTheme.colors.onSecondary
                                )
                            }
                            
                            Button(
                                onClick = { viewModel.toggleBookmark() },
                                colors = if (uiState.isBookmarked) {
                                    ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.primary
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.secondary
                                    )
                                },
                                modifier = Modifier.size(40.dp),
                                shape = Shapes.small
                            ) {
                                Icon(
                                    imageVector = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = if (uiState.isBookmarked) {
                                        stringResource(R.string.remove_bookmark)
                                    } else {
                                        stringResource(R.string.add_bookmark)
                                    },
                                    tint = if (uiState.isBookmarked) {
                                        MaterialTheme.colors.onPrimary
                                    } else {
                                        MaterialTheme.colors.onSecondary
                                    }
                                )
                            }
                            
                            OfflineDownloadButton(
                                isAvailableOffline = uiState.isAvailableOffline,
                                isDownloading = uiState.isDownloading,
                                downloadProgress = uiState.downloadProgress,
                                downloadError = uiState.downloadError,
                                onDownloadClick = { viewModel.downloadBookForOffline() },
                                onRemoveClick = { viewModel.removeOfflineBook() },
                                modifier = Modifier.size(40.dp)
                            )
                            
                            Button(
                                onClick = { 
                                    if (currentIndex < uiState.chapters.size - 1) {
                                        viewModel.loadChapter(uiState.chapters[currentIndex + 1].id)
                                    }
                                },
                                enabled = currentIndex < uiState.chapters.size - 1,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondary
                                ),
                                modifier = Modifier.size(40.dp),
                                shape = Shapes.small
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = stringResource(R.string.next_chapter),
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