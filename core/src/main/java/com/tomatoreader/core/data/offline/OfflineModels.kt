package com.tomatoreader.core.data.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_chapters")
data class OfflineChapter(
    @PrimaryKey
    val id: String,
    val bookId: String,
    val chapterIndex: Int,
    val title: String,
    val content: String,
    val wordCount: Int,
    val downloadedAt: Long
)

@Entity(tableName = "offline_books")
data class OfflineBook(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val coverImageUrl: String?,
    val description: String,
    val totalChapters: Int,
    val lastDownloadedChapter: Int,
    val isFullyDownloaded: Boolean,
    val downloadedAt: Long
)