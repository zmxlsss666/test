package com.tomatoreader.wear.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.data.BookEntity
import com.tomatoreader.core.data.ChapterEntity
import com.tomatoreader.core.repository.BookRepository
import com.tomatoreader.core.repository.ChapterRepository
import com.tomatoreader.core.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReaderUiState(
    val isLoading: Boolean = false,
    val book: BookEntity? = null,
    val chapters: List<ChapterEntity> = emptyList(),
    val currentChapter: ChapterEntity? = null,
    val currentChapterContent: String = "",
    val isBookmarked: Boolean = false,
    val isAvailableOffline: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadError: String? = null,
    val error: String? = null
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()
    
    private var currentBookId: String = ""
    
    fun loadBook(bookId: String) {
        currentBookId = bookId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Load book details
                val book = bookRepository.getBookById(bookId)
                
                // Check if book is available offline first
                val isAvailableOffline = bookRepository.isBookAvailableOffline(bookId)
                
                // Load chapters
                val chapters = chapterRepository.getChapters(bookId)
                
                // Load reading progress
                val progress = bookRepository.getReadingProgress(bookId)
                
                // Find current chapter
                val currentChapter = if (progress != null) {
                    chapters.find { it.id == progress.chapterId }
                } else {
                    chapters.firstOrNull()
                }
                
                // Load current chapter content
                var currentChapterContent = ""
                if (currentChapter != null) {
                    currentChapterContent = chapterRepository.getChapterContent(currentChapter.id)
                }
                
                // Check if current position is bookmarked
                val isBookmarked = if (currentChapter != null) {
                    bookmarkRepository.isBookmarked(bookId, currentChapter.id, 0)
                } else {
                    false
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    book = book,
                    chapters = chapters,
                    currentChapter = currentChapter,
                    currentChapterContent = currentChapterContent,
                    isBookmarked = isBookmarked,
                    isAvailableOffline = isAvailableOffline,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            try {
                // Load chapter content
                val chapterContent = chapterRepository.getChapterContent(chapterId)
                
                // Update reading progress
                bookRepository.updateReadingProgress(currentBookId, chapterId, 0)
                
                // Check if position is bookmarked
                val isBookmarked = bookmarkRepository.isBookmarked(currentBookId, chapterId, 0)
                
                // Find chapter in list
                val chapter = _uiState.value.chapters.find { it.id == chapterId }
                
                _uiState.value = _uiState.value.copy(
                    currentChapter = chapter,
                    currentChapterContent = chapterContent,
                    isBookmarked = isBookmarked
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleBookmark() {
        val currentChapter = _uiState.value.currentChapter ?: return
        
        viewModelScope.launch {
            try {
                if (_uiState.value.isBookmarked) {
                    // Remove bookmark
                    val bookmarks = bookmarkRepository.getBookmarksForBook(currentBookId)
                    val bookmark = bookmarks.find { 
                        it.chapterId == currentChapter.id && it.position == 0 
                    }
                    if (bookmark != null) {
                        bookmarkRepository.deleteBookmark(bookmark.id)
                    }
                } else {
                    // Add bookmark
                    bookmarkRepository.addBookmark(
                        bookId = currentBookId,
                        chapterId = currentChapter.id,
                        chapterTitle = currentChapter.title,
                        chapterIndex = 0, // 需要从chapters列表中获取索引
                        position = 0,
                        note = ""
                    )
                }
                
                _uiState.value = _uiState.value.copy(
                    isBookmarked = !_uiState.value.isBookmarked
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleBookmark(chapterId: String) {
        viewModelScope.launch {
            try {
                val isBookmarked = bookmarkRepository.isChapterBookmarked(_uiState.value.book?.id ?: "", chapterId)
                
                if (isBookmarked) {
                    bookmarkRepository.removeBookmark(_uiState.value.book?.id ?: "", chapterId)
                } else {
                    bookmarkRepository.addBookmark(_uiState.value.book?.id ?: "", chapterId)
                }
                
                // Update the bookmark status in UI state
                val updatedChapters = _uiState.value.chapters.map { chapter ->
                    if (chapter.id == chapterId) {
                        chapter.copy(isBookmarked = !isBookmarked)
                    } else {
                        chapter
                    }
                }
                
                _uiState.value = _uiState.value.copy(chapters = updatedChapters)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun downloadBookForOffline() {
        val bookId = _uiState.value.book?.id ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isDownloading = true, downloadError = null)
                
                bookRepository.downloadBookForOffline(bookId) { progress ->
                    _uiState.value = _uiState.value.copy(downloadProgress = progress)
                }
                
                _uiState.value = _uiState.value.copy(
                    isDownloading = false,
                    isAvailableOffline = true,
                    downloadProgress = 1f
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDownloading = false,
                    downloadError = e.message
                )
            }
        }
    }
    
    fun removeOfflineBook() {
        val bookId = _uiState.value.book?.id ?: return
        
        viewModelScope.launch {
            try {
                bookRepository.removeOfflineBook(bookId)
                
                _uiState.value = _uiState.value.copy(
                    isAvailableOffline = false,
                    downloadProgress = 0f
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun refreshOfflineStatus() {
        val bookId = _uiState.value.book?.id ?: return
        
        viewModelScope.launch {
            try {
                val isAvailableOffline = bookRepository.isBookAvailableOffline(bookId)
                _uiState.value = _uiState.value.copy(isAvailableOffline = isAvailableOffline)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}