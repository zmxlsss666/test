package com.tomatoreader.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 书签实体
 */
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val bookId: String,
    val chapterId: String,
    val chapterTitle: String,
    val chapterIndex: Int,
    val position: Int, // 在章节中的位置（字符偏移）
    val note: String = "", // 书签备注
    val createTime: Long = System.currentTimeMillis()
)