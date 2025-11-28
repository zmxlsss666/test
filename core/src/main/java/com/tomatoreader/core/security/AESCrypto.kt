package com.tomatoreader.core.security

import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.Hex
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AES加密解密工具类
 * 基于Python代码中的加密逻辑实现
 */
@Singleton
class AESCrypto @Inject constructor() {
    
    private val secureRandom = SecureRandom()
    
    /**
     * AES加密
     */
    fun encrypt(data: String, key: String): String {
        try {
            // 生成随机IV
            val iv = ByteArray(16)
            secureRandom.nextBytes(iv)
            
            // 准备加密器
            val cipher = PaddedBufferedBlockCipher(CBCBlockCipher(AESEngine()))
            val keyBytes = Hex.decode(key)
            cipher.init(true, ParametersWithIV(KeyParameter(keyBytes), iv))
            
            // 加密数据
            val dataBytes = data.toByteArray(Charsets.UTF_8)
            val encryptedBytes = ByteArray(cipher.getOutputSize(dataBytes.size))
            var processedBytes = cipher.processBytes(dataBytes, 0, dataBytes.size, encryptedBytes, 0)
            processedBytes += cipher.doFinal(encryptedBytes, processedBytes)
            
            // 组合IV和加密数据，然后Base64编码
            val result = ByteArray(iv.size + processedBytes)
            System.arraycopy(iv, 0, result, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, result, iv.size, processedBytes)
            
            return Base64.toBase64String(result)
        } catch (e: Exception) {
            throw RuntimeException("加密失败", e)
        }
    }
    
    /**
     * AES解密
     */
    fun decrypt(encryptedData: String, key: String): String {
        try {
            // Base64解码
            val dataWithIv = Base64.decode(encryptedData)
            
            // 提取IV和加密数据
            val iv = ByteArray(16)
            val encryptedBytes = ByteArray(dataWithIv.size - 16)
            System.arraycopy(dataWithIv, 0, iv, 0, 16)
            System.arraycopy(dataWithIv, 16, encryptedBytes, 0, encryptedBytes.size)
            
            // 准备解密器
            val cipher = PaddedBufferedBlockCipher(CBCBlockCipher(AESEngine()))
            val keyBytes = Hex.decode(key)
            cipher.init(false, ParametersWithIV(KeyParameter(keyBytes), iv))
            
            // 解密数据
            val decryptedBytes = ByteArray(cipher.getOutputSize(encryptedBytes.size))
            var processedBytes = cipher.processBytes(encryptedBytes, 0, encryptedBytes.size, decryptedBytes, 0)
            processedBytes += cipher.doFinal(decryptedBytes, processedBytes)
            
            return String(decryptedBytes, 0, processedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw RuntimeException("解密失败", e)
        }
    }
    
    /**
     * 生成随机AES密钥
     */
    fun generateKey(): String {
        val keyBytes = ByteArray(16)
        secureRandom.nextBytes(keyBytes)
        return Hex.toHexString(keyBytes)
    }
}