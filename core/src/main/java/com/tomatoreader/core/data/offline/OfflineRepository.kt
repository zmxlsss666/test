package com.tomatoreader.core.data.offline

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRepository @Inject constructor(
    private val offlineBookDao: OfflineBookDao,
    private val offlineChapterDao: OfflineChapterDao
) {
    // Book operations
    suspend fun insertOfflineBook(book: OfflineBook) = offlineBookDao.insertBook(book)
    
    fun getAllOfflineBooks(): Flow<List<OfflineBook>> = offlineBookDao.getAllOfflineBooks()
    
    suspend fun getOfflineBookById(bookId: String): OfflineBook? = offlineBookDao.getOfflineBookById(bookId)
    
    suspend fun updateDownloadProgress(bookId: String, chapterIndex: Int, isFullyDownloaded: Boolean) = 
        offlineBookDao.updateDownloadProgress(bookId, chapterIndex, isFullyDownloaded)
    
    suspend fun deleteOfflineBook(bookId: String) {
        offlineBookDao.deleteOfflineBook(bookId)
        offlineChapterDao.deleteChaptersForBook(bookId)
    }
    
    suspend fun isBookOffline(bookId: String): Boolean = offlineBookDao.isBookOffline(bookId)
    
    // Chapter operations
    suspend fun insertOfflineChapter(chapter: OfflineChapter) = offlineChapterDao.insertChapter(chapter)
    
    suspend fun insertOfflineChapters(chapters: List<OfflineChapter>) = offlineChapterDao.insertChapters(chapters)
    
    fun getOfflineChaptersForBook(bookId: String): Flow<List<OfflineChapter>> = 
        offlineChapterDao.getChaptersForBook(bookId)
    
    suspend fun getOfflineChapterById(chapterId: String): OfflineChapter? = 
        offlineChapterDao.getChapterById(chapterId)
    
    suspend fun getOfflineChapterByIndex(bookId: String, chapterIndex: Int): OfflineChapter? = 
        offlineChapterDao.getChapterByIndex(bookId, chapterIndex)
    
    suspend fun getChapterCountForBook(bookId: String): Int = offlineChapterDao.getChapterCountForBook(bookId)
}