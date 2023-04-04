package jp.co.yumemi.android.code_check.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import retrofit2.Response

inline fun <reified T : Any> apiFlow(crossinline call: suspend () -> Response<T>): Flow<HttpRequestState<T>> =
    flow<HttpRequestState<T>> {
        val response = call()
        if (response.isSuccessful) emit(HttpRequestState.Success(value = response.body()!!))
        else throw HttpException(response)
    }.catch { it: Throwable ->
        emit(HttpRequestState.Error(it))
    }.onStart {
        emit(HttpRequestState.Proceeding)
    }.flowOn(Dispatchers.IO)

inline fun <reified T : Any?> apiNullableFlow(crossinline call: suspend () -> Response<T?>): Flow<HttpRequestState<T?>> =
    flow<HttpRequestState<T?>> {
        val response = call()
        if (response.isSuccessful) emit(HttpRequestState.Success(value = response.body()))
        else throw HttpException(response)
    }.catch { it: Throwable ->
        emit(HttpRequestState.Error(it))
    }.onStart {
        emit(HttpRequestState.Proceeding)
    }.flowOn(Dispatchers.IO)

sealed class HttpRequestState<out T> {
    object Proceeding : HttpRequestState<Nothing>()
    data class Success<out T>(val value: T) : HttpRequestState<T>()
    data class Error(val error: Throwable) : HttpRequestState<Nothing>()
}