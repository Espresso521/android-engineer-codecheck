package jp.co.yumemi.android.code_check.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.yumemi.android.code_check.BuildConfig
import jp.co.yumemi.android.code_check.api.BaseUrlInterceptor
import jp.co.yumemi.android.code_check.api.IApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun providePostApi(retrofit: Retrofit): IApi =
        retrofit.create(IApi::class.java)

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(90, TimeUnit.SECONDS)
            readTimeout(90, TimeUnit.SECONDS)
            writeTimeout(90, TimeUnit.SECONDS)
        }.addInterceptor(HttpLoggingInterceptor { message ->
            if (BuildConfig.DEBUG) {
                val logStr = if(message.length > 2048) message.substring(0, 2048) else message
                Log.d("OkHttpClientLog", logStr)
            }
        }.setLevel(HttpLoggingInterceptor.Level.BODY)).addInterceptor(BaseUrlInterceptor())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
}