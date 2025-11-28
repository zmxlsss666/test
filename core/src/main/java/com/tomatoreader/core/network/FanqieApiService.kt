package com.tomatoreader.core.network

import com.tomatoreader.core.model.BookDetail
import com.tomatoreader.core.model.BookSearchResult
import com.tomatoreader.core.model.CatalogItem
import com.tomatoreader.core.model.ChapterContent
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * 番茄小说API接口定义
 * 基于Python API代码分析实现
 */
interface FanqieApiService {
    
    /**
     * 搜索书籍
     * 对应Python中的handle_book_search函数
     */
    @GET
    suspend fun searchBooks(
        @Url url: String,
        @Query("keyword") keyword: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("device_id") deviceId: String,
        @Query("aid") aid: String = "1967",
        @Query("version_code") versionCode: String = "100",
        @Query("app_name") appName: String = "fanqie_android",
        @Query("version_name") versionName: String = "4.7.0.1",
        @Query("device_type") deviceType: String = "android",
        @Query("device_platform") devicePlatform: String = "android",
        @Query("os_version") osVersion: String = "13",
        @Query("device_brand") deviceBrand: String = "samsung",
        @Query("device_model") deviceModel: String = "SM-G991B",
        @Query("resolution") resolution: String = "2400*1080",
        @Query("dpi") dpi: String = "440",
        @Query("android_id") androidId: String,
        @Query("update_version_code") updateVersionCode: String = "100",
        @Query("_rticket") rticket: String,
        @Query("_signature") signature: String
    ): Response<BookSearchResult>
    
    /**
     * 获取书籍详情
     * 对应Python中的handle_get_detail函数
     */
    @GET
    suspend fun getBookDetail(
        @Url url: String,
        @Query("item_id") itemId: String,
        @Query("device_id") deviceId: String,
        @Query("aid") aid: String = "1967",
        @Query("version_code") versionCode: String = "100",
        @Query("app_name") appName: String = "fanqie_android",
        @Query("version_name") versionName: String = "4.7.0.1",
        @Query("device_type") deviceType: String = "android",
        @Query("device_platform") devicePlatform: String = "android",
        @Query("os_version") osVersion: String = "13",
        @Query("device_brand") deviceBrand: String = "samsung",
        @Query("device_model") deviceModel: String = "SM-G991B",
        @Query("resolution") resolution: String = "2400*1080",
        @Query("dpi") dpi: String = "440",
        @Query("android_id") androidId: String,
        @Query("update_version_code") updateVersionCode: String = "100",
        @Query("_rticket") rticket: String,
        @Query("_signature") signature: String
    ): Response<BookDetail>
    
    /**
     * 获取目录
     * 对应Python中的handle_get_catalog函数
     */
    @GET
    suspend fun getCatalog(
        @Url url: String,
        @Query("item_id") itemId: String,
        @Query("device_id") deviceId: String,
        @Query("aid") aid: String = "1967",
        @Query("version_code") versionCode: String = "100",
        @Query("app_name") appName: String = "fanqie_android",
        @Query("version_name") versionName: String = "4.7.0.1",
        @Query("device_type") deviceType: String = "android",
        @Query("device_platform") devicePlatform: String = "android",
        @Query("os_version") osVersion: String = "13",
        @Query("device_brand") deviceBrand: String = "samsung",
        @Query("device_model") deviceModel: String = "SM-G991B",
        @Query("resolution") resolution: String = "2400*1080",
        @Query("dpi") dpi: String = "440",
        @Query("android_id") androidId: String,
        @Query("update_version_code") updateVersionCode: String = "100",
        @Query("_rticket") rticket: String,
        @Query("_signature") signature: String
    ): Response<List<CatalogItem>>
    
    /**
     * 获取章节内容
     * 对应Python中的handle_get_content函数
     */
    @GET
    suspend fun getChapterContent(
        @Url url: String,
        @Query("item_id") itemId: String,
        @Query("chapter_id") chapterId: String,
        @Query("device_id") deviceId: String,
        @Query("aid") aid: String = "1967",
        @Query("version_code") versionCode: String = "100",
        @Query("app_name") appName: String = "fanqie_android",
        @Query("version_name") versionName: String = "4.7.0.1",
        @Query("device_type") deviceType: String = "android",
        @Query("device_platform") devicePlatform: String = "android",
        @Query("os_version") osVersion: String = "13",
        @Query("device_brand") deviceBrand: String = "samsung",
        @Query("device_model") deviceModel: String = "SM-G991B",
        @Query("resolution") resolution: String = "2400*1080",
        @Query("dpi") dpi: String = "440",
        @Query("android_id") androidId: String,
        @Query("update_version_code") updateVersionCode: String = "100",
        @Query("_rticket") rticket: String,
        @Query("_signature") signature: String
    ): Response<ChapterContent>
}