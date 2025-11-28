package com.tomatoreader.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.model.BookItem
import com.tomatoreader.core.network.FanqieApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍搜索视图模型
 */
@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val apiRepository: FanqieApiRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<BookItem>>(emptyList())
    val searchResults: StateFlow<List<BookItem>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateState()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()
    
    /**
     * 更新搜索查询
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * 搜索书籍
     */
    fun searchBooks() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            _hasSearched.value = false
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _hasSearched.value = true
            
            try {
                val result = apiRepository.searchBooks(query)
                
                if (result.isSuccess) {
                    _searchResults.value = result.getOrNull()?.data?.bookList ?: emptyList()
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "搜索失败"
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "搜索时发生错误"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 清除搜索结果
     */
    fun clearSearchResults() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _hasSearched.value = false
        _errorMessage.value = null
    }
    
    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}