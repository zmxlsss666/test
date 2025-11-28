package com.tomatoreader.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tomatoreader.core.database.dao.BookDao
import com.tomatoreader.core.database.dao.BookmarkDao
import com.tomatoreader.core.database.dao.ChapterDao
import com.tomatoreader.core.database.dao.ReadingProgressDao
import com.tomatoreader.core.database.entity.BookEntity
import com.tomatoreader.core.database.entity.BookmarkEntity
import com.tomatoreader.core.database.entity.ChapterEntity
import com.tomatoreader.core.database.entity.ReadingProgressEntity
import com.tomatoreader.core.data.offline.OfflineBook
import com.tomatoreader.core.data.offline.OfflineChapter
import com.tomatoreader.core.data.offline.OfflineBookDao
import com.tomatoreader.core.data.offline.OfflineChapterDao

/**
 * 应用数据库
 */
@Database(
    entities = [
        BookEntity::class,
        ChapterEntity::class,
        BookmarkEntity::class,
        ReadingProgressEntity::class,
        OfflineBook::class,
        OfflineChapter::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun offlineBookDao(): OfflineBookDao
    abstract fun offlineChapterDao(): OfflineChapterDao
}