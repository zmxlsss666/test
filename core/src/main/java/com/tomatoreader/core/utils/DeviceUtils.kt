package com.tomatoreader.core.utils

import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设备信息工具类
 * 用于生成设备ID、Android ID等信息
 */
@Singleton
class DeviceUtils @Inject constructor(
    private val context: Context
) {
    
    private val deviceId: String by lazy {
        generateDeviceId()
    }
    
    private val androidId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
    }
    
    /**
     * 获取设备ID
     */
    fun getDeviceId(): String = deviceId
    
    /**
     * 获取Android ID
     */
    fun getAndroidId(): String = androidId
    
    /**
     * 获取设备品牌
     */
    fun getDeviceBrand(): String = android.os.Build.BRAND.lowercase(Locale.getDefault())
    
    /**
     * 获取设备型号
     */
    fun getDeviceModel(): String = android.os.Build.MODEL
    
    /**
     * 获取操作系统版本
     */
    fun getOsVersion(): String = android.os.Build.VERSION.RELEASE
    
    /**
     * 获取屏幕分辨率
     */
    fun getResolution(): String {
        val displayMetrics = context.resources.displayMetrics
        return "${displayMetrics.widthPixels}*${displayMetrics.heightPixels}"
    }
    
    /**
     * 获取屏幕DPI
     */
    fun getDpi(): String = context.resources.displayMetrics.densityDpi.toString()
    
    /**
     * 生成设备ID
     * 基于Python代码中的device_register.py逻辑实现
     */
    private fun generateDeviceId(): String {
        // 尝试获取真实的设备ID
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            @Suppress("DEPRECATION")
            val deviceId = telephonyManager.deviceId
            
            if (!deviceId.isNullOrEmpty()) {
                return deviceId
            }
        } catch (e: Exception) {
            // 忽略异常，使用备用方案
        }
        
        // 备用方案：生成随机设备ID
        return UUID.randomUUID().toString().replace("-", "")
    }
    
    /**
     * 生成随机设备信息
     * 用于模拟不同设备
     */
    fun generateRandomDeviceInfo(): Map<String, String> {
        val brands = listOf("samsung", "xiaomi", "huawei", "oppo", "vivo", "oneplus")
        val models = mapOf(
            "samsung" to listOf("SM-G991B", "SM-G998B", "SM-A525F", "SM-S901B"),
            "xiaomi" to listOf("Mi 11", "Mi 10", "Redmi Note 10", "POCO F3"),
            "huawei" to listOf("P40 Pro", "Mate 40 Pro", "P30 Pro", "Mate 30 Pro"),
            "oppo" to listOf("Find X3 Pro", "Reno6 Pro", "Find X2 Pro", "Reno5 Pro"),
            "vivo" to listOf("X60 Pro", "X50 Pro", "iQOO 7", "X70 Pro"),
            "oneplus" to listOf("9 Pro", "8 Pro", "9RT", "8T")
        )
        
        val selectedBrand = brands.random()
        val selectedModel = models[selectedBrand]?.random() ?: "Unknown"
        
        return mapOf(
            "brand" to selectedBrand,
            "model" to selectedModel,
            "os_version" to "13",
            "resolution" to "2400*1080",
            "dpi" to "440"
        )
    }
}