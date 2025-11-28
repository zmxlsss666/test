package com.tomatoreader.wear.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.data.BookmarkEntity
import com.tomatoreader.core.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarkUiState(
    val isLoading: Boolean = false,
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()
    
    fun loadBookmarks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                bookmarkRepository.getAllBookmarks().collect { bookmarks ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        bookmarks = bookmarks,
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
    
    fun deleteBookmark(bookmarkId: String) {
        viewModelScope.launch {
            try {
                bookmarkRepository.deleteBookmark(bookmarkId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun addBookmark(
        bookId: String,
        chapterId: String,
        chapterTitle: String,
        position: Int,
        note: String
    ) {
        viewModelScope.launch {
            try {
                bookmarkRepository.addBookmark(
                    bookId = bookId,
                    chapterId = chapterId,
                    chapterTitle = chapterTitle,
                    position = position,
                    note = note
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}