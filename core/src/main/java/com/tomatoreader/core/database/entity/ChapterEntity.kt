package com.tomatoreader.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 章节实体
 */
@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey
    val id: String,
    val bookId: String,
    val chapterId: String,
    val chapterTitle: String,
    val chapterIndex: Int,
    val wordCount: Long,
    val isVip: Boolean,
    val createTime: Long,
    val updateTime: Long,
    val content: String = "", // 章节内容，用于离线阅读
    val isDownloaded: Boolean = false, // 是否已下载
    val isAvailableOffline: Boolean = false, // 是否可离线阅读
    val isBookmarked: Boolean = false // 是否已添加书签
)