package com.tomatoreader.core.data.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: OfflineChapter)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<OfflineChapter>)
    
    @Query("SELECT * FROM offline_chapters WHERE bookId = :bookId ORDER BY chapterIndex ASC")
    fun getChaptersForBook(bookId: String): Flow<List<OfflineChapter>>
    
    @Query("SELECT * FROM offline_chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: String): OfflineChapter?
    
    @Query("SELECT * FROM offline_chapters WHERE bookId = :bookId AND chapterIndex = :chapterIndex")
    suspend fun getChapterByIndex(bookId: String, chapterIndex: Int): OfflineChapter?
    
    @Query("DELETE FROM offline_chapters WHERE bookId = :bookId")
    suspend fun deleteChaptersForBook(bookId: String)
    
    @Query("SELECT COUNT(*) FROM offline_chapters WHERE bookId = :bookId")
    suspend fun getChapterCountForBook(bookId: String): Int
}

@Dao
interface OfflineBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: OfflineBook)
    
    @Query("SELECT * FROM offline_books ORDER BY downloadedAt DESC")
    fun getAllOfflineBooks(): Flow<List<OfflineBook>>
    
    @Query("SELECT * FROM offline_books WHERE id = :bookId")
    suspend fun getOfflineBookById(bookId: String): OfflineBook?
    
    @Query("UPDATE offline_books SET lastDownloadedChapter = :chapterIndex, isFullyDownloaded = :isFullyDownloaded WHERE id = :bookId")
    suspend fun updateDownloadProgress(bookId: String, chapterIndex: Int, isFullyDownloaded: Boolean)
    
    @Query("DELETE FROM offline_books WHERE id = :bookId")
    suspend fun deleteOfflineBook(bookId: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM offline_books WHERE id = :bookId)")
    suspend fun isBookOffline(bookId: String): Boolean
}