package com.tomatoreader.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomatoreader.core.model.BookItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 书籍搜索页面组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    searchResults: Flow<List<BookItem>>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBookClick: (BookItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchResultsList by searchResults.collectAsState(initial = emptyList())
    var isSearching by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("搜索书籍") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        isSearching = true
                        onSearch()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                }
            }
        )
        
        // 搜索结果
        Box(modifier = Modifier.fillMaxSize()) {
            if (isSearching && searchResultsList.isEmpty()) {
                // 搜索中
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "正在搜索...",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (searchQuery.isNotEmpty() && searchResultsList.isEmpty()) {
                // 无结果
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "未找到相关书籍",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (searchResultsList.isNotEmpty()) {
                // 搜索结果列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(searchResultsList) { book ->
                        SearchResultItem(
                            book = book,
                            onClick = { onBookClick(book) }
                        )
                    }
                }
            } else {
                // 初始状态
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "输入书名或作者名搜索",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 搜索结果项组件
 */
@Composable
fun SearchResultItem(
    book: BookItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 书籍封面
            if (book.cover.isNotEmpty()) {
                // 这里应该加载图片，暂时用占位符
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 16.dp)
                ) {
                    // 使用Coil或Glide加载图片
                    // AsyncImage(model = book.cover, contentDescription = book.bookName)
                }
            }
            
            // 书籍信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.bookName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                Text(
                    text = book.author,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                
                if (book.description.isNotEmpty()) {
                    Text(
                        text = book.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                // 标签
                Row(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    if (book.wordCount > 0) {
                        Text(
                            text = "${book.wordCount}字",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    if (book.readCount > 0) {
                        Text(
                            text = "${book.readCount}人阅读",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    book.category?.let { category ->
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}