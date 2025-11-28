package com.tomatoreader.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 阅读进度实体
 */
@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey
    val bookId: String,
    val chapterId: String,
    val chapterIndex: Int,
    val position: Int, // 在章节中的位置（字符偏移）
    val lastReadTime: Long = System.currentTimeMillis()
)