package jp.co.yumemi.android.code_check.api

import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get the request URL and determine the new base URL based on the host
        val requestUrl = originalRequest.url

        if(requestUrl.toString().contains("v2/auth/login/")) {
            val newBaseUrl = "kotaku-blog.link"

            // Create a new URL with the modified base URL
            val newUrl = originalRequest.url.newBuilder()
                .scheme("https")
                .host(newBaseUrl)
                .build()

            // Create a new request with the modified URL
            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            return chain.proceed(newRequest)
        } else {
            return chain.proceed(originalRequest)
        }
    }
}