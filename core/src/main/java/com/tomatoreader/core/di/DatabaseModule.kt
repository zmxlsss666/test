package com.tomatoreader.core.di

import android.content.Context
import androidx.room.Room
import com.tomatoreader.core.database.AppDatabase
import com.tomatoreader.core.database.dao.BookDao
import com.tomatoreader.core.database.dao.BookmarkDao
import com.tomatoreader.core.database.dao.ChapterDao
import com.tomatoreader.core.database.dao.ReadingProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tomato_reader_database"
        ).fallbackToDestructiveMigration()
         .build()
    }
    
    @Provides
    fun provideBookDao(appDatabase: AppDatabase): BookDao {
        return appDatabase.bookDao()
    }
    
    @Provides
    fun provideChapterDao(appDatabase: AppDatabase): ChapterDao {
        return appDatabase.chapterDao()
    }
    
    @Provides
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao()
    }
    
    @Provides
    fun provideReadingProgressDao(appDatabase: AppDatabase): ReadingProgressDao {
        return appDatabase.readingProgressDao()
    }
}