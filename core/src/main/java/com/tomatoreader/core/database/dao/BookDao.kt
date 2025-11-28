package com.tomatoreader.core.database.dao

import androidx.room.*
import com.tomatoreader.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

/**
 * 书籍数据访问对象
 */
@Dao
interface BookDao {
    
    @Query("SELECT * FROM books ORDER BY lastReadTime DESC")
    fun getAllBooks(): Flow<List<BookEntity>>
    
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): BookEntity?
    
    @Query("SELECT * FROM books WHERE itemId = :itemId")
    suspend fun getBookByItemId(itemId: String): BookEntity?
    
    @Query("SELECT * FROM books WHERE isAvailableOffline = 1 ORDER BY lastReadTime DESC")
    fun getOfflineBooks(): Flow<List<BookEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long
    
    @Update
    suspend fun updateBook(book: BookEntity)
    
    @Delete
    suspend fun deleteBook(book: BookEntity)
    
    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: String)
    
    @Query("UPDATE books SET lastReadTime = :timestamp WHERE id = :id")
    suspend fun updateLastReadTime(id: String, timestamp: Long)
    
    @Query("UPDATE books SET coverImage = :coverImage WHERE id = :id")
    suspend fun updateCoverImage(id: String, coverImage: ByteArray?)
    
    @Query("UPDATE books SET isAvailableOffline = 1 WHERE id = :id")
    suspend fun markBookAsAvailableOffline(id: String)
    
    @Query("UPDATE books SET isAvailableOffline = 0 WHERE id = :id")
    suspend fun unmarkBookAsAvailableOffline(id: String)
    
    @Query("UPDATE books SET isDownloading = :isDownloading, downloadProgress = :progress WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloading: Boolean, progress: Float)
}