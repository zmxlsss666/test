package com.tomatoreader.core.repository

import com.tomatoreader.core.database.dao.BookmarkDao
import com.tomatoreader.core.database.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书签仓库
 * 负责管理书签数据
 */
@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {
    
    /**
     * 获取书籍的所有书签
     */
    fun getBookmarksByBookId(bookId: String): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getBookmarksByBookId(bookId)
    }
    
    /**
     * 根据ID获取书签
     */
    suspend fun getBookmarkById(id: String): BookmarkEntity? {
        return bookmarkDao.getBookmarkById(id)
    }
    
    /**
     * 添加书签
     */
    suspend fun addBookmark(
        bookId: String,
        chapterId: String,
        chapterTitle: String,
        chapterIndex: Int,
        position: Int,
        note: String = ""
    ): Result<BookmarkEntity> {
        return try {
            val bookmark = BookmarkEntity(
                id = UUID.randomUUID().toString(),
                bookId = bookId,
                chapterId = chapterId,
                chapterTitle = chapterTitle,
                chapterIndex = chapterIndex,
                position = position,
                note = note,
                createTime = System.currentTimeMillis()
            )
            
            bookmarkDao.insertBookmark(bookmark)
            Result.success(bookmark)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新书签
     */
    suspend fun updateBookmark(bookmark: BookmarkEntity): Result<Unit> {
        return try {
            bookmarkDao.updateBookmark(bookmark)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除书签
     */
    suspend fun deleteBookmark(bookmarkId: String): Result<Unit> {
        return try {
            bookmarkDao.deleteBookmarkById(bookmarkId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除书籍的所有书签
     */
    suspend fun deleteBookmarksByBookId(bookId: String): Result<Unit> {
        return try {
            bookmarkDao.deleteBookmarksByBookId(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取书籍的书签数量
     */
    suspend fun getBookmarkCountByBookId(bookId: String): Int {
        return bookmarkDao.getBookmarkCountByBookId(bookId)
    }
    
    /**
     * 检查指定位置是否已添加书签
     */
    suspend fun isBookmarked(bookId: String, chapterId: String, position: Int): Boolean {
        return bookmarkDao.isBookmarked(bookId, chapterId, position)
    }
    
    /**
     * 检查章节是否已添加书签
     */
    suspend fun isChapterBookmarked(bookId: String, chapterId: String): Boolean {
        return bookmarkDao.isChapterBookmarked(bookId, chapterId)
    }
    
    /**
     * 获取书籍的所有书签（用于ViewModel）
     */
    suspend fun getBookmarksForBook(bookId: String): List<BookmarkEntity> {
        return bookmarkDao.getBookmarksByBookIdSync(bookId)
    }
    
    /**
     * 添加书签（简化版本，用于ViewModel）
     */
    suspend fun addBookmark(
        bookId: String,
        chapterId: String
    ): Result<BookmarkEntity> {
        return try {
            // 获取章节信息
            val bookmarks = bookmarkDao.getBookmarksByBookId(bookId)
            val chapterIndex = bookmarks.size // 使用书签数量作为章节索引
            
            val bookmark = BookmarkEntity(
                id = UUID.randomUUID().toString(),
                bookId = bookId,
                chapterId = chapterId,
                chapterTitle = "", // 章节标题需要从其他地方获取
                chapterIndex = chapterIndex,
                position = 0,
                note = "",
                createTime = System.currentTimeMillis()
            )
            
            bookmarkDao.insertBookmark(bookmark)
            Result.success(bookmark)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 移除章节书签
     */
    suspend fun removeBookmark(bookId: String, chapterId: String): Result<Unit> {
        return try {
            bookmarkDao.deleteBookmarkByChapterId(bookId, chapterId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}