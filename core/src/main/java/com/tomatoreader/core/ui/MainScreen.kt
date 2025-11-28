package com.tomatoreader.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 主页面组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onBookshelfTabSelected: () -> Unit,
    onSearchTabSelected: () -> Unit,
    onSettingsTabSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    
    Column(modifier = modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = {
                Text(text = "番茄阅读器")
            },
            actions = {
                IconButton(onClick = { /* TODO: 添加同步功能 */ }) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "同步"
                    )
                }
            }
        )
        
        // 内容区域
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> BookshelfTab(onBookshelfTabSelected)
                1 -> SearchTab(onSearchTabSelected)
                2 -> SettingsTab(onSettingsTabSelected)
            }
        }
        
        // 底部导航栏
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.MenuBook, contentDescription = "书架") },
                label = { Text("书架") },
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                label = { Text("搜索") },
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
                label = { Text("设置") },
                selected = pagerState.currentPage == 2,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
        }
    }
}

/**
 * 书架标签页
 */
@Composable
fun BookshelfTab(
    onBookshelfTabSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onBookshelfTabSelected()
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("书架内容将在这里显示")
    }
}

/**
 * 搜索标签页
 */
@Composable
fun SearchTab(
    onSearchTabSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onSearchTabSelected()
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("搜索内容将在这里显示")
    }
}

/**
 * 设置标签页
 */
@Composable
fun SettingsTab(
    onSettingsTabSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onSettingsTabSelected()
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Divider()
        
        // 阅读设置
        SettingSection(
            title = "阅读设置",
            icon = Icons.Default.MenuBook
        ) {
            SettingItem(
                title = "字体大小",
                subtitle = "调整阅读字体大小",
                onClick = { /* TODO: 实现字体大小调整 */ }
            )
            
            SettingItem(
                title = "背景颜色",
                subtitle = "选择阅读背景颜色",
                onClick = { /* TODO: 实现背景颜色选择 */ }
            )
            
            SettingItem(
                title = "翻页模式",
                subtitle = "选择翻页方式",
                onClick = { /* TODO: 实现翻页模式选择 */ }
            )
        }
        
        // 下载设置
        SettingSection(
            title = "下载设置",
            icon = Icons.Default.Download
        ) {
            SettingItem(
                title = "自动下载",
                subtitle = "自动下载后续章节",
                onClick = { /* TODO: 实现自动下载设置 */ }
            )
            
            SettingItem(
                title = "下载位置",
                subtitle = "设置章节下载存储位置",
                onClick = { /* TODO: 实现下载位置设置 */ }
            )
        }
        
        // 其他设置
        SettingSection(
            title = "其他",
            icon = Icons.Default.MoreHoriz
        ) {
            SettingItem(
                title = "关于",
                subtitle = "应用版本和开发者信息",
                onClick = { /* TODO: 实现关于页面 */ }
            )
        }
    }
}

/**
 * 设置分组
 */
@Composable
fun SettingSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        content()
    }
}

/**
 * 设置项
 */
@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "更多"
            )
        }
    }
}