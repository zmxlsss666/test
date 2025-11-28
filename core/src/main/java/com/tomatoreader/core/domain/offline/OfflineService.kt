package com.tomatoreader.core.domain.offline

import android.content.Context
import com.tomatoreader.core.data.offline.OfflineBook
import com.tomatoreader.core.data.offline.OfflineChapter
import com.tomatoreader.core.data.offline.OfflineRepository
import com.tomatoreader.core.data.remote.FanqieApiRepository
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.database.entity.ChapterEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineService @Inject constructor(
    private val offlineRepository: OfflineRepository,
    private val fanqieApiRepository: FanqieApiRepository,
    @ApplicationContext private val context: Context
) {
    // Get all offline books
    fun getOfflineBooks(): Flow<List<OfflineBook>> = offlineRepository.getAllOfflineBooks()
    
    // Check if a book is available offline
    suspend fun isBookAvailableOffline(bookId: String): Boolean = 
        offlineRepository.isBookOffline(bookId)
    
    // Get offline chapters for a book
    fun getOfflineChaptersForBook(bookId: String): Flow<List<OfflineChapter>> = 
        offlineRepository.getOfflineChaptersForBook(bookId)
    
    // Get a specific offline chapter
    suspend fun getOfflineChapter(bookId: String, chapterIndex: Int): OfflineChapter? = 
        offlineRepository.getOfflineChapterByIndex(bookId, chapterIndex)
    
    // Download a book for offline reading
    suspend fun downloadBookForOffline(
        bookId: String,
        onProgress: (Int, Int) -> Unit = { _, _ -> } // (current, total)
    ): Result<Unit> {
        return try {
            // First, get the book details
            val bookDetails = fanqieApiRepository.getBookDetails(bookId).getOrThrow()
            
            // Create offline book record
            val offlineBook = OfflineBook(
                id = bookId,
                title = bookDetails.title,
                author = bookDetails.author,
                coverImageUrl = bookDetails.coverImageUrl,
                description = bookDetails.description,
                totalChapters = bookDetails.totalChapters,
                lastDownloadedChapter = 0,
                isFullyDownloaded = false,
                downloadedAt = System.currentTimeMillis()
            )
            offlineRepository.insertOfflineBook(offlineBook)
            
            // Get all chapters
            val chapters = fanqieApiRepository.getChapters(bookId).getOrThrow()
            
            // Download chapters one by one
            chapters.forEachIndexed { index, chapter ->
                try {
                    // Get chapter content
                    val chapterContent = fanqieApiRepository.getChapterContent(chapter.id).getOrThrow()
                    
                    // Create offline chapter
                    val offlineChapter = OfflineChapter(
                        id = chapter.id,
                        bookId = bookId,
                        chapterIndex = index,
                        title = chapter.title,
                        content = chapterContent.content,
                        wordCount = chapterContent.wordCount,
                        downloadedAt = System.currentTimeMillis()
                    )
                    
                    // Save chapter
                    offlineRepository.insertOfflineChapter(offlineChapter)
                    
                    // Update progress
                    onProgress(index + 1, chapters.size)
                    
                    // Update book download progress
                    offlineRepository.updateDownloadProgress(
                        bookId = bookId,
                        chapterIndex = index + 1,
                        isFullyDownloaded = index + 1 == chapters.size
                    )
                } catch (e: Exception) {
                    // Continue with next chapter even if one fails
                    continue
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Download a single chapter
    suspend fun downloadChapterForOffline(
        bookId: String,
        chapterId: String,
        chapterIndex: Int
    ): Result<OfflineChapter> {
        return try {
            // Get chapter content
            val chapterContent = fanqieApiRepository.getChapterContent(chapterId).getOrThrow()
            
            // Create offline chapter
            val offlineChapter = OfflineChapter(
                id = chapterId,
                bookId = bookId,
                chapterIndex = chapterIndex,
                title = chapterContent.title,
                content = chapterContent.content,
                wordCount = chapterContent.wordCount,
                downloadedAt = System.currentTimeMillis()
            )
            
            // Save chapter
            offlineRepository.insertOfflineChapter(offlineChapter)
            
            // Update book download progress if needed
            val offlineBook = offlineRepository.getOfflineBookById(bookId)
            if (offlineBook != null) {
                val isFullyDownloaded = chapterIndex + 1 >= offlineBook.totalChapters
                offlineRepository.updateDownloadProgress(
                    bookId = bookId,
                    chapterIndex = chapterIndex + 1,
                    isFullyDownloaded = isFullyDownloaded
                )
            }
            
            Result.success(offlineChapter)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Remove a book from offline storage
    suspend fun removeOfflineBook(bookId: String): Result<Unit> {
        return try {
            offlineRepository.deleteOfflineBook(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get storage information
    suspend fun getStorageInfo(): StorageInfo {
        return try {
            // Get app's data directory
            val dataDir = context.filesDir
            val totalSize = getTotalStorageSize(dataDir)
            val usedSize = getUsedStorageSize(dataDir)
            
            StorageInfo(
                totalSize = formatFileSize(totalSize),
                usedSize = formatFileSize(usedSize)
            )
        } catch (e: Exception) {
            StorageInfo(
                totalSize = "未知",
                usedSize = "未知"
            )
        }
    }
    
    private fun getTotalStorageSize(directory: File): Long {
        return try {
            // Get total storage space of the device
            directory.totalSpace
        } catch (e: Exception) {
            0L
        }
    }
    
    private fun getUsedStorageSize(directory: File): Long {
        return try {
            // Calculate used space by app's data directory
            var size: Long = 0
            directory.walkTopDown().forEach { file ->
                size += if (file.isFile) file.length() else 0L
            }
            size
        } catch (e: Exception) {
            0L
        }
    }
    
    private fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> "%.1f GB".format(gb)
            mb >= 1 -> "%.1f MB".format(mb)
            kb >= 1 -> "%.1f KB".format(kb)
            else -> "$bytes B"
        }
    }
}

data class StorageInfo(
    val totalSize: String,
    val usedSize: String
)