package com.tomatoreader.core.database.dao

import androidx.room.*
import com.tomatoreader.core.database.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

/**
 * 书签数据访问对象
 */
@Dao
interface BookmarkDao {
    
    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY createTime DESC")
    fun getBookmarksByBookId(bookId: String): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY createTime DESC")
    suspend fun getBookmarksByBookIdSync(bookId: String): List<BookmarkEntity>
    
    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: String): BookmarkEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long
    
    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)
    
    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)
    
    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)
    
    @Query("DELETE FROM bookmarks WHERE bookId = :bookId")
    suspend fun deleteBookmarksByBookId(bookId: String)
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE bookId = :bookId")
    suspend fun getBookmarkCountByBookId(bookId: String): Int
    
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE bookId = :bookId AND chapterId = :chapterId AND position = :position)")
    suspend fun isBookmarked(bookId: String, chapterId: String, position: Int): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE bookId = :bookId AND chapterId = :chapterId)")
    suspend fun isChapterBookmarked(bookId: String, chapterId: String): Boolean
    
    @Query("DELETE FROM bookmarks WHERE bookId = :bookId AND chapterId = :chapterId")
    suspend fun deleteBookmarkByChapterId(bookId: String, chapterId: String)
}