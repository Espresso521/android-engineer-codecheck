package jp.co.yumemi.android.code_check.api

sealed class Future<out T> {
    object Proceeding : Future<Nothing>()
    data class Success<out T>(val value: T) : Future<T>()
    data class Error(val error: Throwable) : Future<Nothing>()

    fun <R> transform(transform: (T) -> R): Future<R> {
        return when (this) {
            is Success -> Success(transform(this.value))
            is Error -> Error(this.error)
            is Proceeding -> Proceeding
        }
    }
}