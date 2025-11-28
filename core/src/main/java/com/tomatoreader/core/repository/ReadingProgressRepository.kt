package com.tomatoreader.core.repository

import com.tomatoreader.core.database.dao.ReadingProgressDao
import com.tomatoreader.core.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 阅读进度仓库
 * 负责管理阅读进度数据
 */
@Singleton
class ReadingProgressRepository @Inject constructor(
    private val readingProgressDao: ReadingProgressDao
) {
    
    /**
     * 获取所有阅读进度
     */
    fun getAllReadingProgress(): Flow<List<ReadingProgressEntity>> {
        return readingProgressDao.getAllReadingProgress()
    }
    
    /**
     * 获取书籍的阅读进度
     */
    suspend fun getReadingProgressByBookId(bookId: String): ReadingProgressEntity? {
        return readingProgressDao.getReadingProgressByBookId(bookId)
    }
    
    /**
     * 更新阅读进度
     */
    suspend fun updateReadingProgress(
        bookId: String,
        chapterId: String,
        chapterIndex: Int,
        position: Int
    ): Result<Unit> {
        return try {
            readingProgressDao.updateProgress(
                bookId = bookId,
                chapterIndex = chapterIndex,
                position = position,
                lastReadTime = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 设置或更新阅读进度
     */
    suspend fun setReadingProgress(
        bookId: String,
        chapterId: String,
        chapterIndex: Int,
        position: Int
    ): Result<Unit> {
        return try {
            val existingProgress = readingProgressDao.getReadingProgressByBookId(bookId)
            
            if (existingProgress != null) {
                // 更新现有进度
                val updatedProgress = existingProgress.copy(
                    chapterId = chapterId,
                    chapterIndex = chapterIndex,
                    position = position,
                    lastReadTime = System.currentTimeMillis()
                )
                readingProgressDao.updateReadingProgress(updatedProgress)
            } else {
                // 创建新进度
                val newProgress = ReadingProgressEntity(
                    bookId = bookId,
                    chapterId = chapterId,
                    chapterIndex = chapterIndex,
                    position = position,
                    lastReadTime = System.currentTimeMillis()
                )
                readingProgressDao.insertReadingProgress(newProgress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除书籍的阅读进度
     */
    suspend fun deleteReadingProgressByBookId(bookId: String): Result<Unit> {
        return try {
            readingProgressDao.deleteReadingProgressByBookId(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}