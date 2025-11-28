package com.tomatoreader.core.database.dao

import androidx.room.*
import com.tomatoreader.core.database.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

/**
 * 章节数据访问对象
 */
@Dao
interface ChapterDao {
    
    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY chapterIndex")
    fun getChaptersByBookId(bookId: String): Flow<List<ChapterEntity>>
    
    @Query("SELECT * FROM chapters WHERE bookId = :bookId AND chapterIndex = :chapterIndex")
    suspend fun getChapterByIndex(bookId: String, chapterIndex: Int): ChapterEntity?
    
    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: String): ChapterEntity?
    
    @Query("SELECT * FROM chapters WHERE bookId = :bookId AND chapterId = :chapterId")
    suspend fun getChapterByChapterId(bookId: String, chapterId: String): ChapterEntity?
    
    @Query("SELECT * FROM chapters WHERE bookId = :bookId AND isAvailableOffline = 1 ORDER BY chapterIndex")
    fun getOfflineChaptersByBookId(bookId: String): Flow<List<ChapterEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)
    
    @Update
    suspend fun updateChapter(chapter: ChapterEntity)
    
    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)
    
    @Query("DELETE FROM chapters WHERE bookId = :bookId")
    suspend fun deleteChaptersByBookId(bookId: String)
    
    @Query("SELECT COUNT(*) FROM chapters WHERE bookId = :bookId")
    suspend fun getChapterCountByBookId(bookId: String): Int
    
    @Query("UPDATE chapters SET content = :content, isDownloaded = 1 WHERE id = :id")
    suspend fun updateChapterContent(id: String, content: String)
    
    @Query("SELECT * FROM chapters WHERE bookId = :bookId AND isDownloaded = 1 ORDER BY chapterIndex")
    fun getDownloadedChaptersByBookId(bookId: String): Flow<List<ChapterEntity>>
    
    @Query("UPDATE chapters SET isAvailableOffline = 1 WHERE id = :id")
    suspend fun markChapterAsOffline(id: String)
    
    @Query("UPDATE chapters SET isAvailableOffline = 0 WHERE id = :id")
    suspend fun unmarkChapterAsOffline(id: String)
}