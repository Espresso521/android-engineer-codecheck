package jp.co.yumemi.android.code_check.api

import jp.co.yumemi.android.code_check.data.model.Login
import jp.co.yumemi.android.code_check.data.model.ResponseResult
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IApi {

    @POST("v2/auth/token/login/")
    suspend fun login(@Body body: RequestBody): Response<ResponseResult<Login>>

}