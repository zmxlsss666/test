package com.tomatoreader.wear.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.data.BookEntity
import com.tomatoreader.core.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookEntityWithOfflineStatus(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val lastReadTime: Long,
    val lastReadChapter: String,
    val readingProgress: Float,
    val isAvailableOffline: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadError: String? = null
)

data class BookshelfUiState(
    val isLoading: Boolean = false,
    val books: List<BookEntityWithOfflineStatus> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookshelfUiState())
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()
    
    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                bookRepository.getBookshelfBooks().collect { books ->
                    val booksWithOfflineStatus = books.map { book ->
                        val isAvailableOffline = bookRepository.isBookAvailableOffline(book.id)
                        BookEntityWithOfflineStatus(
                            id = book.id,
                            title = book.title,
                            author = book.author,
                            coverUrl = book.coverUrl,
                            lastReadTime = book.lastReadTime,
                            lastReadChapter = book.lastReadChapter,
                            readingProgress = book.readingProgress,
                            isAvailableOffline = isAvailableOffline,
                            isDownloading = false,
                            downloadProgress = 0f,
                            downloadError = null
                        )
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        books = booksWithOfflineStatus,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun downloadBookForOffline(bookId: String) {
        viewModelScope.launch {
            try {
                // 更新下载状态
                val currentBooks = _uiState.value.books.toMutableList()
                val bookIndex = currentBooks.indexOfFirst { it.id == bookId }
                if (bookIndex >= 0) {
                    currentBooks[bookIndex] = currentBooks[bookIndex].copy(
                        isDownloading = true,
                        downloadProgress = 0f,
                        downloadError = null
                    )
                    _uiState.value = _uiState.value.copy(books = currentBooks)
                }
                
                // 开始下载
                bookRepository.downloadBookForOffline(
                    bookId = bookId,
                    onProgress = { current, total ->
                        val progress = if (total > 0) current.toFloat() / total else 0f
                        val updatedBooks = _uiState.value.books.toMutableList()
                        val index = updatedBooks.indexOfFirst { it.id == bookId }
                        if (index >= 0) {
                            updatedBooks[index] = updatedBooks[index].copy(downloadProgress = progress)
                            _uiState.value = _uiState.value.copy(books = updatedBooks)
                        }
                    }
                )
                
                // 下载完成
                val completedBooks = _uiState.value.books.toMutableList()
                val completedIndex = completedBooks.indexOfFirst { it.id == bookId }
                if (completedIndex >= 0) {
                    completedBooks[completedIndex] = completedBooks[completedIndex].copy(
                        isDownloading = false,
                        isAvailableOffline = true,
                        downloadProgress = 1f
                    )
                    _uiState.value = _uiState.value.copy(books = completedBooks)
                }
            } catch (e: Exception) {
                // 下载失败
                val failedBooks = _uiState.value.books.toMutableList()
                val failedIndex = failedBooks.indexOfFirst { it.id == bookId }
                if (failedIndex >= 0) {
                    failedBooks[failedIndex] = failedBooks[failedIndex].copy(
                        isDownloading = false,
                        downloadError = e.message
                    )
                    _uiState.value = _uiState.value.copy(books = failedBooks)
                }
            }
        }
    }
    
    fun removeOfflineBook(bookId: String) {
        viewModelScope.launch {
            try {
                bookRepository.removeOfflineBook(bookId)
                
                // 更新UI状态
                val currentBooks = _uiState.value.books.toMutableList()
                val bookIndex = currentBooks.indexOfFirst { it.id == bookId }
                if (bookIndex >= 0) {
                    currentBooks[bookIndex] = currentBooks[bookIndex].copy(
                        isAvailableOffline = false
                    )
                    _uiState.value = _uiState.value.copy(books = currentBooks)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun addToBookshelf(bookId: String) {
        viewModelScope.launch {
            try {
                bookRepository.addToBookshelf(bookId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun removeFromBookshelf(bookId: String) {
        viewModelScope.launch {
            try {
                bookRepository.removeFromBookshelf(bookId)
                // Refresh the book list
                loadBooks()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}