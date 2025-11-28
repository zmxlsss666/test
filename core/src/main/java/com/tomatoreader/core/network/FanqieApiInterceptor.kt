package com.tomatoreader.core.network

import com.tomatoreader.core.security.XGorgon
import com.tomatoreader.core.utils.DeviceUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 番茄小说API请求拦截器
 * 负责添加必要的请求头，包括X-Gorgon和X-Khronos签名
 * 对应Python中的gorgonRequest方法
 */
@Singleton
class FanqieApiInterceptor @Inject constructor(
    private val xGorgon: XGorgon,
    private val deviceUtils: DeviceUtils
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        
        // 生成时间戳和票据
        val timestamp = System.currentTimeMillis().toString()
        val rticket = UUID.randomUUID().toString().replace("-", "")
        
        // 获取设备信息
        val deviceId = deviceUtils.getDeviceId()
        val androidId = deviceUtils.getAndroidId()
        
        // 构建新的URL，添加必要的查询参数
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("_rticket", rticket)
            .addQueryParameter("_signature", generateSignature(url))
            .build()
        
        // 构建新的请求
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 13; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Mobile Safari/537.36 Fanqie/4.7.0.1")
            .addHeader("Accept", "application/json, text/plain, */*")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            .addHeader("Accept-Encoding", "gzip, deflate, br")
            .addHeader("Origin", "https://fanqienovel.com")
            .addHeader("Referer", "https://fanqienovel.com/")
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("X-Sdk-Version", "2")
            .addHeader("X-Client-Version", "4.7.0.1")
            .addHeader("X-App-Id", "1967")
            .addHeader("X-Device-Type", "android")
            .addHeader("X-Device-Id", deviceId)
            .addHeader("X-Android-Id", androidId)
            .addHeader("X-Timestamp", timestamp)
            .build()
        
        // 生成X-Gorgon和X-Khronos签名
        val signedRequest = xGorgon.signRequest(newRequest)
        
        return chain.proceed(signedRequest)
    }
    
    /**
     * 生成简单的签名
     * 这里实现一个简化的签名算法，实际应用中可能需要更复杂的逻辑
     */
    private fun generateSignature(url: String): String {
        // 简化的签名算法，实际应该根据Python代码中的逻辑实现
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "${timestamp}_${random}_${url.hashCode()}"
    }
}