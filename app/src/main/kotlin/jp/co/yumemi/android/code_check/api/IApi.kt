package jp.co.yumemi.android.code_check.api

import jp.co.yumemi.android.code_check.data.SearchResult
import jp.co.yumemi.android.code_check.data.model.Login
import jp.co.yumemi.android.code_check.data.model.ResponseResult
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IApi {

    @POST("v2/auth/login/")
    suspend fun login(@Body body: RequestBody): Response<ResponseResult<Login>>

    @GET("search/repositories")
    suspend fun search(@Query("q") key: String): SearchResult

}