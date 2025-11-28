package com.tomatoreader.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.database.entity.ChapterEntity
import com.tomatoreader.core.database.entity.ReadingProgressEntity
import com.tomatoreader.core.model.BookItem
import com.tomatoreader.core.repository.BookRepository
import com.tomatoreader.core.repository.ChapterRepository
import com.tomatoreader.core.repository.ReadingProgressRepository
import com.tomatoreader.core.network.FanqieApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 阅读器视图模型
 */
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val apiRepository: FanqieApiRepository
) : ViewModel() {
    
    private val _currentBook = MutableStateFlow<BookEntity?>(null)
    val currentBook: StateFlow<BookEntity?> = _currentBook.asStateFlow()
    
    private val _currentChapterIndex = MutableStateFlow(0)
    val currentChapterIndex: StateFlow<Int> = _currentChapterIndex.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()
    
    private val _chapters = MutableStateFlow<List<ChapterEntity>>(emptyList())
    val chapters: StateFlow<List<ChapterEntity>> = _chapters.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * 设置当前书籍
     */
    fun setCurrentBook(book: BookEntity) {
        _currentBook.value = book
        loadBookChapters(book.itemId, book.id)
        loadReadingProgress(book.id)
    }
    
    /**
     * 加载书籍章节
     */
    private fun loadBookChapters(itemId: String, bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // 首先尝试从本地获取章节
                val localChapters = chapterRepository.getChaptersByBookId(bookId).first()
                
                if (localChapters.isNotEmpty()) {
                    _chapters.value = localChapters
                } else {
                    // 本地没有章节，从网络获取
                    val result = chapterRepository.fetchBookCatalog(itemId, bookId)
                    if (result.isSuccess) {
                        _chapters.value = result.getOrNull() ?: emptyList()
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "获取章节失败"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "加载章节时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 加载阅读进度
     */
    private fun loadReadingProgress(bookId: String) {
        viewModelScope.launch {
            try {
                val progress = readingProgressRepository.getReadingProgressByBookId(bookId)
                if (progress != null) {
                    _currentChapterIndex.value = progress.chapterIndex
                    _currentPosition.value = progress.position
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "加载阅读进度时发生错误"
            }
        }
    }
    
    /**
     * 切换到指定章节
     */
    fun switchToChapter(chapterIndex: Int) {
        if (chapterIndex < 0 || chapterIndex >= _chapters.value.size) return
        
        _currentChapterIndex.value = chapterIndex
        _currentPosition.value = 0
        
        // 加载章节内容
        loadChapterContent(chapterIndex)
        
        // 更新阅读进度
        updateReadingProgress()
    }
    
    /**
     * 加载章节内容
     */
    private fun loadChapterContent(chapterIndex: Int) {
        val chapter = _chapters.value.getOrNull(chapterIndex) ?: return
        val book = _currentBook.value ?: return
        
        viewModelScope.launch {
            try {
                // 如果章节内容为空，从网络获取
                if (chapter.content.isEmpty()) {
                    _isLoading.value = true
                    
                    val result = chapterRepository.getChapterContent(
                        book.itemId, 
                        chapter.chapterId, 
                        book.id
                    )
                    
                    if (result.isFailure) {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "获取章节内容失败"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "加载章节内容时发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 更新阅读位置
     */
    fun updateReadingPosition(position: Int) {
        _currentPosition.value = position
        updateReadingProgress()
    }
    
    /**
     * 更新阅读进度
     */
    private fun updateReadingProgress() {
        val book = _currentBook.value ?: return
        val chapter = _chapters.value.getOrNull(_currentChapterIndex.value) ?: return
        
        viewModelScope.launch {
            try {
                readingProgressRepository.updateReadingProgress(
                    bookId = book.id,
                    chapterId = chapter.chapterId,
                    chapterIndex = _currentChapterIndex.value,
                    position = _currentPosition.value
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "更新阅读进度时发生错误"
            }
        }
    }
    
    /**
     * 获取当前章节
     */
    fun getCurrentChapter(): ChapterEntity? {
        return _chapters.value.getOrNull(_currentChapterIndex.value)
    }
    
    /**
     * 下一章
     */
    fun nextChapter() {
        if (_currentChapterIndex.value < _chapters.value.size - 1) {
            switchToChapter(_currentChapterIndex.value + 1)
        }
    }
    
    /**
     * 上一章
     */
    fun previousChapter() {
        if (_currentChapterIndex.value > 0) {
            switchToChapter(_currentChapterIndex.value - 1)
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}