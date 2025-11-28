package com.tomatoreader.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.model.BookDetail
import com.tomatoreader.core.model.BookItem
import com.tomatoreader.core.repository.BookRepository
import com.tomatoreader.core.network.FanqieApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍详情视图模型
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val apiRepository: FanqieApiRepository
) : ViewModel() {
    
    private val _bookId = MutableStateFlow<String?>(null)
    val bookId: StateFlow<String?> = _bookId.asStateFlow()
    
    private val _bookDetail = MutableStateFlow<BookDetail?>(null)
    val bookDetail: StateFlow<BookDetail?> = _bookDetail.asStateFlow()
    
    private val _isInBookshelf = MutableStateFlow(false)
    val isInBookshelf: StateFlow<Boolean> = _isInBookshelf.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * 设置书籍ID
     */
    fun setBookId(bookId: String) {
        if (_bookId.value != bookId) {
            _bookId.value = bookId
            loadBookDetail()
            checkIfInBookshelf()
        }
    }
    
    /**
     * 设置书籍项（用于从搜索结果跳转）
     */
    fun setBookItem(bookItem: BookItem) {
        _bookId.value = bookItem.itemId
        
        // 创建一个临时的书籍详情对象
        val tempBookDetail = BookDetail(
            code = 0,
            message = "success",
            data = com.tomatoreader.core.model.BookDetailData(
                bookInfo = com.tomatoreader.core.model.BookInfo(
                    itemId = bookItem.itemId,
                    bookName = bookItem.bookName,
                    author = bookItem.author,
                    cover = bookItem.cover,
                    description = bookItem.description,
                    category = bookItem.category,
                    wordCount = bookItem.wordCount,
                    readCount = bookItem.readCount,
                    lastChapterTitle = "",
                    updateTime = 0,
                    createTime = 0,
                    status = 0,
                    isVip = false
                )
            )
        )
        
        _bookDetail.value = tempBookDetail
        checkIfInBookshelf()
    }
    
    /**
     * 加载书籍详情
     */
    private fun loadBookDetail() {
        val bookId = _bookId.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = apiRepository.getBookDetail(bookId)
                
                if (result.isSuccess) {
                    _bookDetail.value = result.getOrNull()
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "获取书籍详情失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "加载书籍详情时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 检查书籍是否已在书架
     */
    private fun checkIfInBookshelf() {
        val bookId = _bookId.value ?: return
        
        viewModelScope.launch {
            try {
                val book = bookRepository.getBookByItemId(bookId)
                _isInBookshelf.value = book != null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "检查书架状态时发生错误"
            }
        }
    }
    
    /**
     * 添加书籍到书架
     */
    fun addBookToBookshelf() {
        val bookDetail = _bookDetail.value ?: return
        val bookInfo = bookDetail.data.bookInfo
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val bookEntity = BookEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    itemId = bookInfo.itemId,
                    bookName = bookInfo.bookName,
                    author = bookInfo.author,
                    coverImage = bookInfo.cover,
                    description = bookInfo.description,
                    category = bookInfo.category,
                    wordCount = bookInfo.wordCount,
                    readCount = bookInfo.readCount,
                    lastChapterTitle = bookInfo.lastChapterTitle,
                    updateTime = bookInfo.updateTime,
                    createTime = bookInfo.createTime,
                    status = bookInfo.status,
                    isVip = bookInfo.isVip,
                    lastReadTime = 0,
                    isDownloaded = false
                )
                
                bookRepository.addBook(bookEntity)
                _isInBookshelf.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "添加书籍到书架时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 从书架移除书籍
     */
    fun removeBookFromBookshelf() {
        val bookId = _bookId.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val book = bookRepository.getBookByItemId(bookId)
                if (book != null) {
                    bookRepository.removeBook(book.id)
                    _isInBookshelf.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "从书架移除书籍时发生错误"
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