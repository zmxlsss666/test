package com.tomatoreader.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.database.entity.ReadingProgressEntity
import com.tomatoreader.core.repository.BookRepository
import com.tomatoreader.core.repository.ReadingProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书架视图模型
 */
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingProgressRepository: ReadingProgressRepository
) : ViewModel() {
    
    /**
     * 书架书籍流
     */
    val books: Flow<List<BookEntity>> = bookRepository.getAllBooks()
    
    /**
     * 阅读进度流
     */
    val readingProgress: Flow<Map<String, ReadingProgressEntity>> = 
        readingProgressRepository.getAllReadingProgress()
            .map { progressList ->
                progressList.associateBy { it.bookId }
            }
    
    /**
     * 添加书籍到书架
     */
    fun addBookToBookshelf(book: BookEntity) {
        viewModelScope.launch {
            bookRepository.addBook(book)
        }
    }
    
    /**
     * 从书架移除书籍
     */
    fun removeBookFromBookshelf(book: BookEntity) {
        viewModelScope.launch {
            bookRepository.removeBook(book.id)
        }
    }
    
    /**
     * 搜索书籍
     */
    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookRepository.searchBooks(query)
    }
    
    /**
     * 更新书籍最后阅读时间
     */
    fun updateLastReadTime(bookId: String) {
        viewModelScope.launch {
            bookRepository.updateLastReadTime(bookId)
        }
    }
}