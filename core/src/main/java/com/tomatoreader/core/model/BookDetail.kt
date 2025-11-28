package com.tomatoreader.core.model

import com.google.gson.annotations.SerializedName

/**
 * 书籍详情模型
 */
data class BookDetail(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: BookDetailData
)

/**
 * 书籍详情数据
 */
data class BookDetailData(
    @SerializedName("item_data")
    val bookInfo: BookInfo
)

/**
 * 书籍详细信息
 */
data class BookInfo(
    @SerializedName("item_id")
    val itemId: String,
    @SerializedName("book_name")
    val bookName: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("word_count")
    val wordCount: Long,
    @SerializedName("read_count")
    val readCount: Long,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("status")
    val status: Int, // 1: 连载中, 2: 已完结
    @SerializedName("create_time")
    val createTime: Long,
    @SerializedName("update_time")
    val updateTime: Long,
    @SerializedName("last_chapter_title")
    val lastChapterTitle: String,
    @SerializedName("last_chapter_id")
    val lastChapterId: String,
    @SerializedName("first_chapter_id")
    val firstChapterId: String,
    @SerializedName("chapter_count")
    val chapterCount: Int,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("score")
    val score: Float
)