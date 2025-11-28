package com.tomatoreader.wear.di

import android.content.Context
import com.tomatoreader.core.database.AppDatabase
import com.tomatoreader.core.repository.BookRepository
import com.tomatoreader.core.repository.ChapterRepository
import com.tomatoreader.core.repository.BookmarkRepository
import com.tomatoreader.core.repository.ReadingProgressRepository
import com.tomatoreader.core.repository.FanqieApiRepository
import com.tomatoreader.core.repository.OfflineService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WearModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideBookDao(database: AppDatabase) = database.bookDao()
    
    @Provides
    fun provideChapterDao(database: AppDatabase) = database.chapterDao()
    
    @Provides
    fun provideBookmarkDao(database: AppDatabase) = database.bookmarkDao()
    
    @Provides
    fun provideReadingProgressDao(database: AppDatabase) = database.readingProgressDao()
    
    @Provides
    @Singleton
    fun provideFanqieApiRepository(): FanqieApiRepository {
        return FanqieApiRepository()
    }
    
    @Provides
    @Singleton
    fun provideOfflineService(@ApplicationContext context: Context): OfflineService {
        return OfflineService(context)
    }
    
    @Provides
    @Singleton
    fun provideBookRepository(
        bookDao: com.tomatoreader.core.database.BookDao,
        apiRepository: FanqieApiRepository,
        readingProgressDao: com.tomatoreader.core.database.ReadingProgressDao,
        offlineService: OfflineService
    ): BookRepository {
        return BookRepository(bookDao, apiRepository, readingProgressDao, offlineService)
    }
    
    @Provides
    @Singleton
    fun provideChapterRepository(
        chapterDao: com.tomatoreader.core.database.ChapterDao,
        apiRepository: FanqieApiRepository,
        offlineService: OfflineService
    ): ChapterRepository {
        return ChapterRepository(chapterDao, apiRepository, offlineService)
    }
    
    @Provides
    @Singleton
    fun provideBookmarkRepository(
        bookmarkDao: com.tomatoreader.core.database.BookmarkDao
    ): BookmarkRepository {
        return BookmarkRepository(bookmarkDao)
    }
    
    @Provides
    @Singleton
    fun provideReadingProgressRepository(
        readingProgressDao: com.tomatoreader.core.database.ReadingProgressDao
    ): ReadingProgressRepository {
        return ReadingProgressRepository(readingProgressDao)
    }
}