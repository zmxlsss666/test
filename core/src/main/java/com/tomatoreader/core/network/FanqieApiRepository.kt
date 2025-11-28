package com.tomatoreader.core.network

import com.tomatoreader.core.model.BookDetail
import com.tomatoreader.core.model.BookSearchResult
import com.tomatoreader.core.model.CatalogItem
import com.tomatoreader.core.model.ChapterContent
import com.tomatoreader.core.utils.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 番茄小说API仓库
 * 提供高级API操作，处理网络请求和数据转换
 */
@Singleton
class FanqieApiRepository @Inject constructor(
    private val apiService: FanqieApiService,
    private val deviceUtils: DeviceUtils
) {
    
    /**
     * 搜索书籍
     */
    suspend fun searchBooks(keyword: String, limit: Int = 20, offset: Int = 0): Result<BookSearchResult> {
        return try {
            withContext(Dispatchers.IO) {
                val deviceId = deviceUtils.getDeviceId()
                val androidId = deviceUtils.getAndroidId()
                val rticket = java.util.UUID.randomUUID().toString().replace("-", "")
                val signature = generateSignature("search")
                
                val response = apiService.searchBooks(
                    url = "https://api5-normal-lq.fqnovel.com/reading/bookapi/search/v/",
                    keyword = keyword,
                    limit = limit,
                    offset = offset,
                    deviceId = deviceId,
                    androidId = androidId,
                    rticket = rticket,
                    signature = signature
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("搜索失败: ${response.code()} ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取书籍详情
     */
    suspend fun getBookDetail(itemId: String): Result<BookDetail> {
        return try {
            withContext(Dispatchers.IO) {
                val deviceId = deviceUtils.getDeviceId()
                val androidId = deviceUtils.getAndroidId()
                val rticket = java.util.UUID.randomUUID().toString().replace("-", "")
                val signature = generateSignature("detail")
                
                val response = apiService.getBookDetail(
                    url = "https://api5-normal-lq.fqnovel.com/reading/bookapi/detail/v/",
                    itemId = itemId,
                    deviceId = deviceId,
                    androidId = androidId,
                    rticket = rticket,
                    signature = signature
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("获取书籍详情失败: ${response.code()} ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取书籍目录
     */
    suspend fun getBookCatalog(itemId: String): Result<List<CatalogItem>> {
        return try {
            withContext(Dispatchers.IO) {
                val deviceId = deviceUtils.getDeviceId()
                val androidId = deviceUtils.getAndroidId()
                val rticket = java.util.UUID.randomUUID().toString().replace("-", "")
                val signature = generateSignature("catalog")
                
                val response = apiService.getCatalog(
                    url = "https://api5-normal-lq.fqnovel.com/reading/bookapi/directory/all_items/v/",
                    itemId = itemId,
                    deviceId = deviceId,
                    androidId = androidId,
                    rticket = rticket,
                    signature = signature
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("获取目录失败: ${response.code()} ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取章节内容
     */
    suspend fun getChapterContent(itemId: String, chapterId: String): Result<ChapterContent> {
        return try {
            withContext(Dispatchers.IO) {
                val deviceId = deviceUtils.getDeviceId()
                val androidId = deviceUtils.getAndroidId()
                val rticket = java.util.UUID.randomUUID().toString().replace("-", "")
                val signature = generateSignature("content")
                
                val response = apiService.getChapterContent(
                    url = "https://api5-normal-lq.fqnovel.com/reading/bookapi/multi_detail/v/",
                    itemId = itemId,
                    chapterId = chapterId,
                    deviceId = deviceId,
                    androidId = androidId,
                    rticket = rticket,
                    signature = signature
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("获取章节内容失败: ${response.code()} ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 生成简单的签名
     */
    private fun generateSignature(action: String): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "${timestamp}_${random}_${action.hashCode()}"
    }
}