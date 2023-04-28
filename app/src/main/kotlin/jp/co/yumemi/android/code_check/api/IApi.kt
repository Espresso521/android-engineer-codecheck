package jp.co.yumemi.android.code_check.api

import jp.co.yumemi.android.code_check.data.SearchResult
import jp.co.yumemi.android.code_check.data.model.Login
import jp.co.yumemi.android.code_check.data.model.ResponseResult
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface IApi {

    @POST("v2/auth/login/")
    suspend fun login(@Body body: RequestBody): Response<ResponseResult<Login>>

    @GET("search/repositories")
    suspend fun search(@Query("q") key: String): Response<SearchResult>

    @GET("search/repositories")
    @Headers("Accept: application/vnd.github.v3+json")
    suspend fun doPageSearch(@Query("q") key: String, @Query("page") page: String): Response<SearchResult>

    @GET
    @Headers("Accept: application/vnd.github.v3+json")
    suspend fun getReadMe(@Url url: String): Response<ResponseBody>

}