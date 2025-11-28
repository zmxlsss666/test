package com.tomatoreader.wear.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.domain.offline.OfflineService
import com.tomatoreader.core.domain.offline.model.OfflineBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 离线管理界面的ViewModel
 */
@HiltViewModel
class OfflineViewModel @Inject constructor(
    private val offlineService: OfflineService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OfflineUiState())
    val uiState: StateFlow<OfflineUiState> = _uiState.asStateFlow()
    
    var downloadProgress by mutableStateOf<Map<String, Float>>(emptyMap())
        private set
    
    init {
        loadStorageInfo()
    }
    
    /**
     * 加载离线书籍列表
     */
    fun loadOfflineBooks() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                val offlineBooks = offlineService.getOfflineBooks()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    offlineBooks = offlineBooks,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载离线书籍失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载存储信息
     */
    private fun loadStorageInfo() {
        viewModelScope.launch {
            try {
                val storageInfo = offlineService.getStorageInfo()
                _uiState.value = _uiState.value.copy(
                    totalStorageSize = storageInfo.totalSize,
                    usedStorageSize = storageInfo.usedSize
                )
            } catch (e: Exception) {
                // 存储信息加载失败不影响主要功能
                _uiState.value = _uiState.value.copy(
                    totalStorageSize = "未知",
                    usedStorageSize = "未知"
                )
            }
        }
    }
    
    /**
     * 下载书籍用于离线阅读
     */
    fun downloadBookForOffline(bookId: String) {
        viewModelScope.launch {
            try {
                // 更新下载状态
                _uiState.value = _uiState.value.copy(
                    isDownloading = true,
                    downloadError = null
                )
                
                // 开始下载
                val result = offlineService.downloadBookForOffline(
                    bookId = bookId,
                    onProgress = { current, total ->
                        val progress = if (total > 0) current.toFloat() / total else 0f
                        downloadProgress = downloadProgress + (bookId to progress)
                    }
                )
                
                if (result.isSuccess) {
                    // 下载成功，刷新列表
                    loadOfflineBooks()
                    loadStorageInfo()
                } else {
                    // 下载失败
                    _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        downloadError = result.exceptionOrNull()?.message ?: "下载失败"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDownloading = false,
                    downloadError = "下载失败: ${e.message}"
                )
            } finally {
                // 清除进度
                downloadProgress = downloadProgress - bookId
            }
        }
    }
    
    /**
     * 移除离线书籍
     */
    fun removeOfflineBook(bookId: String) {
        viewModelScope.launch {
            try {
                val result = offlineService.removeOfflineBook(bookId)
                if (result.isSuccess) {
                    // 移除成功，刷新列表
                    loadOfflineBooks()
                    loadStorageInfo()
                } else {
                    // 移除失败
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.exceptionOrNull()?.message ?: "移除失败"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "移除失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * 离线管理界面的UI状态
 */
data class OfflineUiState(
    val isLoading: Boolean = false,
    val offlineBooks: List<OfflineBook> = emptyList(),
    val isDownloading: Boolean = false,
    val downloadError: String? = null,
    val errorMessage: String? = null,
    val totalStorageSize: String = "0 MB",
    val usedStorageSize: String = "0 MB"
)