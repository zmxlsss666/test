package com.tomatoreader.core.model

import com.google.gson.annotations.SerializedName

/**
 * 目录项模型
 */
data class CatalogItem(
    @SerializedName("item_id")
    val itemId: String,
    @SerializedName("chapter_id")
    val chapterId: String,
    @SerializedName("chapter_title")
    val chapterTitle: String,
    @SerializedName("chapter_index")
    val chapterIndex: Int,
    @SerializedName("word_count")
    val wordCount: Long,
    @SerializedName("is_vip")
    val isVip: Boolean,
    @SerializedName("create_time")
    val createTime: Long,
    @SerializedName("update_time")
    val updateTime: Long
)