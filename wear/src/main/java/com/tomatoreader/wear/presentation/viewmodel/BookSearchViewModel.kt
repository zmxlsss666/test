package com.tomatoreader.wear.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.data.BookItem
import com.tomatoreader.core.repository.FanqieApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookSearchUiState(
    val isLoading: Boolean = false,
    val searchResults: List<BookItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val apiRepository: FanqieApiRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookSearchUiState())
    val uiState: StateFlow<BookSearchUiState> = _uiState.asStateFlow()
    
    fun searchBooks(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val results = apiRepository.searchBooks(query)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    searchResults = results,
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
    
    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            error = null
        )
    }
}