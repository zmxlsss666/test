package com.tomatoreader.core.security

import okhttp3.Request
import org.bouncycastle.crypto.digests.MD5Digest
import org.bouncycastle.util.encoders.Hex
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * X-Gorgon签名生成器
 * 基于Python代码中的XGorgon类实现
 */
@Singleton
class XGorgon @Inject constructor() {
    
    /**
     * 为请求添加X-Gorgon和X-Khronos签名
     */
    fun signRequest(request: Request): Request {
        val timestamp = System.currentTimeMillis().toString()
        val url = request.url.toString()
        val method = request.method
        
        // 生成X-Gorgon签名
        val xGorgon = generateXGorgon(url, method, timestamp)
        
        // 构建新的请求，添加签名头
        return request.newBuilder()
            .addHeader("X-Gorgon", xGorgon)
            .addHeader("X-Khronos", timestamp)
            .build()
    }
    
    /**
     * 生成X-Gorgon签名
     * 基于Python代码中的逻辑实现
     */
    private fun generateXGorgon(url: String, method: String, timestamp: String): String {
        // 提取URL路径部分
        val path = extractPath(url)
        
        // 构建基础字符串
        val baseString = "$method\n$path\n$timestamp\n"
        
        // 计算MD5哈希
        val md5 = md5(baseString)
        
        // 添加随机数和设备信息
        val random = (100000..999999).random()
        val deviceInfo = "android"
        
        // 构建最终签名
        val signature = "$md5$random$deviceInfo"
        
        // 对签名进行Base64编码
        return base64Encode(signature.toByteArray())
    }
    
    /**
     * 从URL中提取路径部分
     */
    private fun extractPath(url: String): String {
        return try {
            val urlObj = java.net.URL(url)
            urlObj.path + if (urlObj.query != null) "?${urlObj.query}" else ""
        } catch (e: Exception) {
            url
        }
    }
    
    /**
     * 计算字符串的MD5哈希
     */
    private fun md5(input: String): String {
        val digest = MD5Digest()
        val bytes = input.toByteArray()
        digest.update(bytes, 0, bytes.size)
        val md5Bytes = ByteArray(digest.digestSize)
        digest.doFinal(md5Bytes, 0)
        return Hex.toHexString(md5Bytes).lowercase(Locale.getDefault())
    }
    
    /**
     * Base64编码
     */
    private fun base64Encode(input: ByteArray): String {
        return android.util.Base64.encodeToString(input, android.util.Base64.NO_WRAP)
    }
}