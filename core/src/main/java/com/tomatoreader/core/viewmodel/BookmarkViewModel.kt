package com.tomatoreader.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.database.entity.BookmarkEntity
import com.tomatoreader.core.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书签视图模型
 */
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    
    private val _bookId = MutableStateFlow<String?>(null)
    val bookId: StateFlow<String?> = _bookId.asStateFlow()
    
    private val _bookmarks = MutableStateFlow<List<BookmarkEntity>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkEntity>> = _bookmarks.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * 设置当前书籍ID
     */
    fun setBookId(bookId: String) {
        if (_bookId.value != bookId) {
            _bookId.value = bookId
            loadBookmarks()
        }
    }
    
    /**
     * 加载书签
     */
    private fun loadBookmarks() {
        val bookId = _bookId.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                bookmarkRepository.getBookmarksByBookId(bookId).collect { bookmarksList ->
                    _bookmarks.value = bookmarksList
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "加载书签时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 添加书签
     */
    fun addBookmark(
        chapterId: String,
        chapterTitle: String,
        chapterIndex: Int,
        position: Int,
        note: String = ""
    ) {
        val bookId = _bookId.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = bookmarkRepository.addBookmark(
                    bookId = bookId,
                    chapterId = chapterId,
                    chapterTitle = chapterTitle,
                    chapterIndex = chapterIndex,
                    position = position,
                    note = note
                )
                
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "添加书签失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "添加书签时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 更新书签
     */
    fun updateBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = bookmarkRepository.updateBookmark(bookmark)
                
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "更新书签失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "更新书签时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 删除书签
     */
    fun deleteBookmark(bookmarkId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = bookmarkRepository.deleteBookmark(bookmarkId)
                
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "删除书签失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "删除书签时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}