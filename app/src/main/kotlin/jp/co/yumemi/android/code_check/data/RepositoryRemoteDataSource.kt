package jp.co.yumemi.android.code_check.data

import jp.co.yumemi.android.code_check.api.IApi
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

    fun doPageSearch(key: String, page: String): Flow<SearchResult> = flow {
        val searchResult = httpsAPI.doPageSearch(key, page)
        if (searchResult.items.isNotEmpty()) {
            emit(searchResult)
        }
    }
}