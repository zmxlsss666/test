package com.tomatoreader.core.database.dao

import androidx.room.*
import com.tomatoreader.core.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * 阅读进度数据访问对象
 */
@Dao
interface ReadingProgressDao {
    
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    suspend fun getReadingProgressByBookId(bookId: String): ReadingProgressEntity?
    
    @Query("SELECT * FROM reading_progress")
    fun getAllReadingProgress(): Flow<List<ReadingProgressEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingProgress(progress: ReadingProgressEntity): Long
    
    @Update
    suspend fun updateReadingProgress(progress: ReadingProgressEntity)
    
    @Query("UPDATE reading_progress SET chapterIndex = :chapterIndex, position = :position, lastReadTime = :lastReadTime WHERE bookId = :bookId")
    suspend fun updateProgress(
        bookId: String,
        chapterIndex: Int,
        position: Int,
        lastReadTime: Long
    )
    
    @Query("DELETE FROM reading_progress WHERE bookId = :bookId")
    suspend fun deleteReadingProgressByBookId(bookId: String)
}