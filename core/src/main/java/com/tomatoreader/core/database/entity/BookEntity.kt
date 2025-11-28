package com.tomatoreader.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 书籍实体
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val itemId: String,
    val bookName: String,
    val author: String,
    val cover: String,
    val description: String,
    val wordCount: Long,
    val readCount: Long,
    val categoryName: String,
    val status: Int, // 1: 连载中, 2: 已完结
    val createTime: Long,
    val updateTime: Long,
    val lastChapterTitle: String,
    val lastChapterId: String,
    val firstChapterId: String,
    val chapterCount: Int,
    val tags: List<String>,
    val score: Float,
    val lastReadTime: Long = System.currentTimeMillis(),
    val addedTime: Long = System.currentTimeMillis(),
    val coverImage: ByteArray? = null, // 本地缓存的封面图片
    val isAvailableOffline: Boolean = false, // 是否可离线阅读
    val isDownloading: Boolean = false, // 是否正在下载
    val downloadProgress: Float = 0f // 下载进度 (0.0 - 1.0)
)