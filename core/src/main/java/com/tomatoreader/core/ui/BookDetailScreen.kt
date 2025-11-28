package com.tomatoreader.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomatoreader.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 书籍详情页面组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: BookEntity,
    isInBookshelf: Boolean,
    onAddToBookshelf: () -> Unit,
    onRemoveFromBookshelf: () -> Unit,
    onStartReading: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = {
                Text(
                    text = "书籍详情",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回"
                    )
                }
            }
        )
        
        // 书籍详情内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 书籍基本信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // 书籍封面
                if (book.coverImage.isNotEmpty()) {
                    // 这里应该加载图片，暂时用占位符
                    Box(
                        modifier = Modifier
                            .size(120.dp)
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "作者: ${book.author}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    if (book.category.isNotEmpty()) {
                        Text(
                            text = "分类: ${book.category}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    if (book.wordCount > 0) {
                        Text(
                            text = "字数: ${book.wordCount}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    if (book.readCount > 0) {
                        Text(
                            text = "阅读量: ${book.readCount}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
            
            // 书籍简介
            if (book.description.isNotEmpty()) {
                Text(
                    text = "简介",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Text(
                    text = book.description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 添加/移除书架按钮
                if (isInBookshelf) {
                    OutlinedButton(
                        onClick = onRemoveFromBookshelf,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("从书架移除")
                    }
                } else {
                    Button(
                        onClick = onAddToBookshelf,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("加入书架")
                    }
                }
                
                // 开始阅读按钮
                Button(
                    onClick = onStartReading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("开始阅读")
                }
            }
        }
    }
}