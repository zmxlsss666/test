package com.tomatoreader.core.repository

import com.tomatoreader.core.database.dao.BookDao
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.model.BookDetail
import com.tomatoreader.core.model.BookItem
import com.tomatoreader.core.network.FanqieApiRepository
import com.tomatoreader.core.domain.offline.OfflineService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书籍仓库
 * 负责管理书籍数据和书架功能
 */
@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val apiRepository: FanqieApiRepository,
    private val offlineService: OfflineService
) {
    
    /**
     * 获取书架上的所有书籍
     */
    fun getBooksOnShelf(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }
    
    /**
     * 根据ID获取书籍
     */
    suspend fun getBookById(id: String): BookEntity? {
        return bookDao.getBookById(id)
    }
    
    /**
     * 根据itemId获取书籍
     */
    suspend fun getBookByItemId(itemId: String): BookEntity? {
        return bookDao.getBookByItemId(itemId)
    }
    
    /**
     * 添加书籍到书架
     */
    suspend fun addBookToShelf(bookItem: BookItem): Result<BookEntity> {
        return try {
            // 检查书籍是否已在书架上
            val existingBook = bookDao.getBookByItemId(bookItem.itemId)
            if (existingBook != null) {
                return Result.failure(Exception("书籍已在书架上"))
            }
            
            // 获取书籍详细信息
            val detailResult = apiRepository.getBookDetail(bookItem.itemId)
            if (detailResult.isFailure) {
                return Result.failure(detailResult.exceptionOrNull() ?: Exception("获取书籍详情失败"))
            }
            
            val bookDetail = detailResult.getOrThrow()
            val bookInfo = bookDetail.data.bookInfo
            
            // 创建书籍实体
            val bookEntity = BookEntity(
                id = UUID.randomUUID().toString(),
                itemId = bookInfo.itemId,
                bookName = bookInfo.bookName,
                author = bookInfo.author,
                cover = bookInfo.cover,
                description = bookInfo.description,
                wordCount = bookInfo.wordCount,
                readCount = bookInfo.readCount,
                categoryName = bookInfo.categoryName,
                status = bookInfo.status,
                createTime = bookInfo.createTime,
                updateTime = bookInfo.updateTime,
                lastChapterTitle = bookInfo.lastChapterTitle,
                lastChapterId = bookInfo.lastChapterId,
                firstChapterId = bookInfo.firstChapterId,
                chapterCount = bookInfo.chapterCount,
                tags = bookInfo.tags,
                score = bookInfo.score
            )
            
            // 保存到数据库
            bookDao.insertBook(bookEntity)
            Result.success(bookEntity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 从书架移除书籍
     */
    suspend fun removeBookFromShelf(bookId: String): Result<Unit> {
        return try {
            bookDao.deleteBookById(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新书籍信息
     */
    suspend fun updateBook(bookEntity: BookEntity): Result<Unit> {
        return try {
            bookDao.updateBook(bookEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新书籍最后阅读时间
     */
    suspend fun updateLastReadTime(bookId: String): Result<Unit> {
        return try {
            bookDao.updateLastReadTime(bookId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新书籍封面图片
     */
    suspend fun updateCoverImage(bookId: String, coverImage: ByteArray?): Result<Unit> {
        return try {
            bookDao.updateCoverImage(bookId, coverImage)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 搜索书籍
     */
    suspend fun searchBooks(keyword: String, limit: Int = 20, offset: Int = 0): Result<List<BookItem>> {
        return try {
            val searchResult = apiRepository.searchBooks(keyword, limit, offset)
            if (searchResult.isSuccess) {
                Result.success(searchResult.getOrThrow().data.bookList)
            } else {
                Result.failure(searchResult.exceptionOrNull() ?: Exception("搜索失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取书籍详细信息
     */
    suspend fun getBookDetail(itemId: String): Result<BookDetail> {
        return apiRepository.getBookDetail(itemId)
    }
    
    /**
     * 检查书籍是否可离线阅读
     */
    suspend fun isBookAvailableOffline(bookId: String): Boolean {
        return offlineService.isBookAvailableOffline(bookId)
    }
    
    /**
     * 下载书籍用于离线阅读
     */
    suspend fun downloadBookForOffline(
        bookId: String,
        onProgress: (Float) -> Unit = { _ -> }
    ): Result<Unit> {
        return try {
            offlineService.downloadBookForOffline(bookId, onProgress)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 移除离线书籍
     */
    suspend fun removeOfflineBook(bookId: String): Result<Unit> {
        return try {
            offlineService.removeOfflineBook(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取离线书籍列表
     */
    fun getOfflineBooks(): Flow<List<BookEntity>> {
        return offlineService.getOfflineBooks()
    }
    
    /**
     * 更新下载状态
     */
    suspend fun updateDownloadStatus(bookId: String, isDownloading: Boolean, progress: Float) {
        bookDao.updateDownloadStatus(bookId, isDownloading, progress)
    }
    
    /**
     * 标记书籍为可离线阅读
     */
    suspend fun markBookAsAvailableOffline(bookId: String) {
        bookDao.markBookAsAvailableOffline(bookId)
    }
    
    /**
     * 取消标记书籍为可离线阅读
     */
    suspend fun unmarkBookAsAvailableOffline(bookId: String) {
        bookDao.unmarkBookAsAvailableOffline(bookId)
    }
    
    /**
     * 获取阅读进度
     */
    suspend fun getReadingProgress(bookId: String): ReadingProgress? {
        // This would typically be implemented with a ReadingProgressDao
        // For now, we'll return null
        return null
    }
    
    /**
     * 更新阅读进度
     */
    suspend fun updateReadingProgress(bookId: String, chapterId: String, position: Int) {
        // This would typically be implemented with a ReadingProgressDao
        // For now, we'll do nothing
    }
}

/**
 * 阅读进度数据类
 */
data class ReadingProgress(
    val bookId: String,
    val chapterId: String,
    val position: Int,
    val lastReadTime: Long
)
}