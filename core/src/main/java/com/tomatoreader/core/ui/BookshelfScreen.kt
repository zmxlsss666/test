package com.tomatoreader.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 书架页面组件
 */
@Composable
fun BookshelfScreen(
    books: Flow<List<BookEntity>>,
    readingProgress: Flow<Map<String, ReadingProgressEntity>>,
    onBookClick: (BookEntity) -> Unit,
    onAddBook: () -> Unit,
    onRemoveBook: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val booksList by books.collectAsState(initial = emptyList())
    val progressMap by readingProgress.collectAsState(initial = emptyMap())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的书架",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Button(onClick = onAddBook) {
                Text("添加书籍")
            }
        }
        
        // 书籍列表
        if (booksList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "书架空空如也",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onAddBook) {
                        Text("添加第一本书")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(booksList) { book ->
                    BookItem(
                        book = book,
                        readingProgress = progressMap[book.id],
                        onClick = { onBookClick(book) },
                        onRemove = { onRemoveBook(book) }
                    )
                }
            }
        }
    }
}

/**
 * 书籍项组件
 */
@Composable
fun BookItem(
    book: BookEntity,
    readingProgress: ReadingProgressEntity?,
    onClick: () -> Unit,
    onRemove: () -> Unit,
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
            if (book.coverImage.isNotEmpty()) {
                // 这里应该加载图片，暂时用占位符
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 16.dp)
                ) {
                    // 使用Coil或Glide加载图片
                    // AsyncImage(model = book.coverImage, contentDescription = book.bookName)
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
                
                // 阅读进度
                readingProgress?.let { progress ->
                    Text(
                        text = "阅读进度: 第${progress.chapterIndex}章",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 删除按钮
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除书籍",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}