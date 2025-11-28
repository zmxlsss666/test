package com.tomatoreader.core.model

import com.google.gson.annotations.SerializedName

/**
 * 章节内容模型
 */
data class ChapterContent(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ChapterContentData
)

/**
 * 章节内容数据
 */
data class ChapterContentData(
    @SerializedName("item_data_list")
    val contentList: List<ChapterContentItem>
)

/**
 * 章节内容项
 */
data class ChapterContentItem(
    @SerializedName("item_id")
    val itemId: String,
    @SerializedName("chapter_id")
    val chapterId: String,
    @SerializedName("chapter_title")
    val chapterTitle: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("word_count")
    val wordCount: Long,
    @SerializedName("is_vip")
    val isVip: Boolean
)