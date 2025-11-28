package com.tomatoreader.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.database.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 阅读器页面组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    book: BookEntity,
    chapters: Flow<List<ChapterEntity>>,
    currentChapterIndex: Int,
    currentPosition: Int,
    onChapterChange: (Int) -> Unit,
    onPositionChange: (Int) -> Unit,
    onBack: () -> Unit,
    onAddBookmark: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chaptersList by chapters.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val currentChapter = chaptersList.getOrNull(currentChapterIndex)
    
    // 监听滚动位置
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        // 计算当前位置并更新
        val position = listState.firstVisibleItemIndex * 1000 + listState.firstVisibleItemScrollOffset
        onPositionChange(position)
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = book.bookName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    currentChapter?.let {
                        Text(
                            text = it.chapterTitle,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }
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
                IconButton(onClick = { 
                    currentChapter?.let { chapter ->
                        onAddBookmark(currentPosition, chapter.chapterTitle)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "添加书签"
                    )
                }
            }
        )
        
        // 章节内容
        if (currentChapter != null && currentChapter.content.isNotEmpty()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = currentChapter.chapterTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // 章节内容
                itemsIndexed(currentChapter.content.split("\n")) { index, paragraph ->
                    if (paragraph.isNotBlank()) {
                        Text(
                            text = paragraph,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        } else {
            // 加载中或无内容
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "正在加载章节内容...",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 章节选择对话框
 */
@Composable
fun ChapterSelectionDialog(
    chapters: List<ChapterEntity>,
    currentChapterIndex: Int,
    onDismiss: () -> Unit,
    onChapterSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "选择章节")
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                itemsIndexed(chapters) { index, chapter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { 
                                onChapterSelected(index)
                                onDismiss()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = index == currentChapterIndex,
                            onClick = { 
                                onChapterSelected(index)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = chapter.chapterTitle,
                                fontSize = 16.sp,
                                fontWeight = if (index == currentChapterIndex) FontWeight.Bold else FontWeight.Normal
                            )
                            if (chapter.wordCount > 0) {
                                Text(
                                    text = "${chapter.wordCount}字",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}