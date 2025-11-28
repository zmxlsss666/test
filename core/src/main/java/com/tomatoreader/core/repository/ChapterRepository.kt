package com.tomatoreader.core.repository

import com.tomatoreader.core.database.dao.ChapterDao
import com.tomatoreader.core.database.entity.ChapterEntity
import com.tomatoreader.core.model.CatalogItem
import com.tomatoreader.core.model.ChapterContent
import com.tomatoreader.core.network.FanqieApiRepository
import com.tomatoreader.core.domain.offline.OfflineService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 章节仓库
 * 负责管理章节数据和阅读内容
 */
@Singleton
class ChapterRepository @Inject constructor(
    private val chapterDao: ChapterDao,
    private val apiRepository: FanqieApiRepository,
    private val offlineService: OfflineService
) {
    
    /**
     * 获取书籍的所有章节
     */
    fun getChapters(bookId: String): Flow<List<ChapterEntity>> {
        return chapterDao.getChaptersByBookId(bookId)
    }
    
    /**
     * 获取书籍的所有章节
     */
    fun getChaptersByBookId(bookId: String): Flow<List<ChapterEntity>> {
        return chapterDao.getChaptersByBookId(bookId)
    }
    
    /**
     * 获取离线章节
     */
    fun getOfflineChapters(bookId: String): Flow<List<ChapterEntity>> {
        return chapterDao.getOfflineChaptersByBookId(bookId)
    }
    
    /**
     * 检查章节是否可离线阅读
     */
    suspend fun isChapterAvailableOffline(chapterId: String): Boolean {
        val chapter = chapterDao.getChapterById(chapterId)
        return chapter?.isAvailableOffline ?: false
    }
    
    /**
     * 标记章节为可离线阅读
     */
    suspend fun markChapterAsOffline(chapterId: String) {
        chapterDao.markChapterAsOffline(chapterId)
    }
    
    /**
     * 取消标记章节为可离线阅读
     */
    suspend fun unmarkChapterAsOffline(chapterId: String) {
        chapterDao.unmarkChapterAsOffline(chapterId)
    }
    
    /**
     * 根据索引获取章节
     */
    suspend fun getChapterByIndex(bookId: String, chapterIndex: Int): ChapterEntity? {
        return chapterDao.getChapterByIndex(bookId, chapterIndex)
    }
    
    /**
     * 根据ID获取章节
     */
    suspend fun getChapterById(id: String): ChapterEntity? {
        return chapterDao.getChapterById(id)
    }
    
    /**
     * 根据章节ID获取章节
     */
    suspend fun getChapterByChapterId(bookId: String, chapterId: String): ChapterEntity? {
        return chapterDao.getChapterByChapterId(bookId, chapterId)
    }
    
    /**
     * 获取书籍目录
     */
    suspend fun fetchBookCatalog(itemId: String, bookId: String): Result<List<ChapterEntity>> {
        return try {
            val catalogResult = apiRepository.getBookCatalog(itemId)
            if (catalogResult.isFailure) {
                return Result.failure(catalogResult.exceptionOrNull() ?: Exception("获取目录失败"))
            }
            
            val catalogItems = catalogResult.getOrThrow()
            val chapterEntities = catalogItems.map { catalogItem ->
                ChapterEntity(
                    id = UUID.randomUUID().toString(),
                    bookId = bookId,
                    chapterId = catalogItem.chapterId,
                    chapterTitle = catalogItem.chapterTitle,
                    chapterIndex = catalogItem.chapterIndex,
                    wordCount = catalogItem.wordCount,
                    isVip = catalogItem.isVip,
                    createTime = catalogItem.createTime,
                    updateTime = catalogItem.updateTime
                )
            }
            
            // 保存到数据库
            chapterDao.insertChapters(chapterEntities)
            Result.success(chapterEntities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取章节内容
     */
    suspend fun getChapterContent(chapterId: String): String {
        // 首先检查本地是否有已下载的内容
        val localChapter = chapterDao.getChapterById(chapterId)
        if (localChapter != null && localChapter.isAvailableOffline && localChapter.content.isNotEmpty()) {
            return localChapter.content
        }
        
        // 如果没有离线内容，需要从网络获取
        // 这里简化处理，实际应用中需要从网络API获取内容
        throw Exception("章节内容不可用，请先下载")
    }
    
    /**
     * 检查章节是否可离线阅读
     */
    suspend fun isChapterAvailableOffline(bookId: String, chapterId: String): Boolean {
        return offlineService.isChapterAvailableOffline(bookId, chapterId)
    }
    
    /**
     * 获取已下载的章节
     */
    fun getDownloadedChaptersByBookId(bookId: String): Flow<List<ChapterEntity>> {
        return chapterDao.getDownloadedChaptersByBookId(bookId)
    }
    
    /**
     * 获取章节总数
     */
    suspend fun getChapterCountByBookId(bookId: String): Int {
        return chapterDao.getChapterCountByBookId(bookId)
    }
    
    /**
     * 删除书籍的所有章节
     */
    suspend fun deleteChaptersByBookId(bookId: String): Result<Unit> {
        return try {
            chapterDao.deleteChaptersByBookId(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}