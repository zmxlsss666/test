package com.tomatoreader.core.di

import com.tomatoreader.core.network.FanqieApiInterceptor
import com.tomatoreader.core.network.FanqieApiRepository
import com.tomatoreader.core.network.FanqieApiService
import com.tomatoreader.core.security.AESCrypto
import com.tomatoreader.core.security.XGorgon
import com.tomatoreader.core.utils.DeviceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 核心模块的依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        fanqieApiInterceptor: FanqieApiInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(fanqieApiInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api5-normal-lq.fqnovel.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideFanqieApiService(retrofit: Retrofit): FanqieApiService {
        return retrofit.create(FanqieApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideFanqieApiRepository(
        apiService: FanqieApiService,
        deviceUtils: DeviceUtils
    ): FanqieApiRepository {
        return FanqieApiRepository(apiService, deviceUtils)
    }
    
    @Provides
    @Singleton
    fun provideXGorgon(): XGorgon {
        return XGorgon()
    }
    
    @Provides
    @Singleton
    fun provideAESCrypto(): AESCrypto {
        return AESCrypto()
    }
}