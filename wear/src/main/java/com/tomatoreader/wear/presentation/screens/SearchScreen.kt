package com.tomatoreader.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import com.tomatoreader.wear.R
import com.tomatoreader.wear.presentation.components.BookItem
import com.tomatoreader.wear.presentation.components.RotaryAwareScalingLazyColumn
import com.tomatoreader.wear.presentation.theme.Shapes
import com.tomatoreader.wear.presentation.viewmodel.BookSearchViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onBookClick: (String) -> Unit,
    viewModel: BookSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.Top) }
    ) {
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
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        backgroundColor = MaterialTheme.colors.primaryContainer
                    )
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
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colors.onSecondary
                            )
                        }
                        
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.title3,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        backgroundColor = MaterialTheme.colors.surfaceVariant
                    )
                ) {
                    ValueBox(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                "输入书名或作者",
                                color = MaterialTheme.colors.onSurfaceVariant
                            ) 
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    viewModel.searchBooks(searchQuery)
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            colors = CircularProgressIndicatorDefaults.colors(
                                indicatorColor = MaterialTheme.colors.primary
                            )
                        )
                    }
                }
            } else if (uiState.error != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            backgroundColor = MaterialTheme.colors.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
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
                                onClick = { 
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.searchBooks(searchQuery)
                                    }
                                },
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
                }
            } else if (uiState.searchResults.isEmpty() && searchQuery.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            backgroundColor = MaterialTheme.colors.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "未找到相关书籍",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            } else if (uiState.searchResults.isNotEmpty()) {
                items(uiState.searchResults) { book ->
                    BookItem(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            } else if (searchQuery.isBlank()) {
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
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "请输入搜索关键词",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}