package jp.co.yumemi.android.code_check.data

import io.ktor.client.*
import jp.co.yumemi.android.code_check.api.IApi
import jp.co.yumemi.android.code_check.api.IApiImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RepositoryRemoteDataSource @Inject constructor(
    private val httpsAPI: IApi
) {
    fun getSearchResult(key: String): Flow<SearchResult> = flow {
        val searchResult = httpsAPI.search(key)
        emit(searchResult)
    }
}