package com.tomatoreader.core.repository

import android.content.Context
import androidx.work.*
import com.tomatoreader.core.data.BookEntity
import com.tomatoreader.core.data.ChapterEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository
) {
    
    fun downloadBookForOffline(bookId: String, progressCallback: (Float) -> Unit) {
        // This would typically use a WorkManager or Foreground Service for actual downloading
        // For simplicity, we'll simulate the download process
        try {
            // Mark book as being downloaded
            bookRepository.updateDownloadStatus(bookId, true, 0f)
            
            // Get book and chapters
            val book = bookRepository.getBookById(bookId)
            val chapters = chapterRepository.getChapters(bookId)
            
            // Simulate downloading chapters
            val totalChapters = chapters.size
            chapters.forEachIndexed { index, chapter ->
                // Simulate download progress
                val progress = (index + 1).toFloat() / totalChapters
                progressCallback(progress)
                
                // In a real implementation, you would download the chapter content here
                // and save it to local storage
                
                // Mark chapter as available offline
                chapterRepository.markChapterAsOffline(chapter.id)
                
                // Simulate delay
                Thread.sleep(100)
            }
            
            // Mark book as available offline
            bookRepository.markBookAsAvailableOffline(bookId)
            bookRepository.updateDownloadStatus(bookId, false, 1f)
        } catch (e: Exception) {
            // Mark download as failed
            bookRepository.updateDownloadStatus(bookId, false, 0f)
            throw e
        }
    }
    
    fun removeOfflineBook(bookId: String) {
        try {
            // Get chapters
            val chapters = chapterRepository.getChapters(bookId)
            
            // Remove offline content for all chapters
            chapters.forEach { chapter ->
                chapterRepository.unmarkChapterAsOffline(chapter.id)
            }
            
            // Mark book as not available offline
            bookRepository.unmarkBookAsAvailableOffline(bookId)
        } catch (e: Exception) {
            throw e
        }
    }
    
    fun isBookAvailableOffline(bookId: String): Boolean {
        return bookRepository.isBookAvailableOffline(bookId)
    }
    
    fun isChapterAvailableOffline(chapterId: String): Boolean {
        return chapterRepository.isChapterAvailableOffline(chapterId)
    }
    
    fun getOfflineBooks(): Flow<List<BookEntity>> {
        return bookRepository.getOfflineBooks()
    }
    
    fun getOfflineChapters(bookId: String): Flow<List<ChapterEntity>> {
        return chapterRepository.getOfflineChapters(bookId)
    }
    
    fun scheduleOfflineDownload(bookId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val downloadRequest = OneTimeWorkRequestBuilder<OfflineDownloadWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf("bookId" to bookId))
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "offline_download_$bookId",
            ExistingWorkPolicy.REPLACE,
            downloadRequest
        )
    }
    
    fun cancelOfflineDownload(bookId: String) {
        WorkManager.getInstance(context).cancelUniqueWork("offline_download_$bookId")
    }
}

class OfflineDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        val bookId = inputData.getString("bookId") ?: return Result.failure()
        
        return try {
            // In a real implementation, you would inject the OfflineService here
            // and call downloadBookForOffline
            
            // For now, we'll just return success
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}