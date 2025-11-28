package com.tomatoreader.core.model

import com.google.gson.annotations.SerializedName

/**
 * 书籍搜索结果模型
 */
data class BookSearchResult(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: BookSearchData
)

/**
 * 书籍搜索数据
 */
data class BookSearchData(
    @SerializedName("total")
    val total: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("item_data_list")
    val bookList: List<BookItem>
)

/**
 * 书籍项
 */
data class BookItem(
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
    val lastChapterTitle: String
)