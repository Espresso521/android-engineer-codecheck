package jp.co.yumemi.android.code_check.api

import android.util.Log
import com.google.gson.GsonBuilder
import jp.co.yumemi.android.code_check.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object IApiImpl {
    private val api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_HOST)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder().apply {
                    connectTimeout(60, TimeUnit.SECONDS)
                    readTimeout(60, TimeUnit.SECONDS)
                    writeTimeout(60, TimeUnit.SECONDS)
                }.addInterceptor(HttpLoggingInterceptor { message ->
                    if (BuildConfig.DEBUG) {
                        val logStr =
                            if (message.length > 2048) message.substring(0, 2048) else message
                        Log.d("OkHttpClientLog", logStr)
                    }
                }.setLevel(HttpLoggingInterceptor.Level.BODY)).addInterceptor(BaseUrlInterceptor())
                    .build()
            )
            .build()
        retrofit.create(IApi::class.java)
    }

    fun get(): IApi {
        return api
    }
}
