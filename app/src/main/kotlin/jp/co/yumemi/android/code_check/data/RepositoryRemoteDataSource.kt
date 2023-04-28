package jp.co.yumemi.android.code_check.data

import jp.co.yumemi.android.code_check.api.IApi
import jp.co.yumemi.android.code_check.viewmodel.LatestReposUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class RepositoryRemoteDataSource @Inject constructor(
    private val httpsAPI: IApi
) {
    fun getSearchResult(key: String): Flow<LatestReposUiState> =
        flow<LatestReposUiState> {
            val searchResult = httpsAPI.search(key)
            if (searchResult.body()!!.items.isNotEmpty()) {
                emit(LatestReposUiState.Success(searchResult.body()!!.items))
            } else {
                throw HttpException(searchResult)
            }
        }.catch { it: Throwable ->
            emit(LatestReposUiState.Error(it))
        }.onStart {
            emit(LatestReposUiState.Loading)
        }

    fun doPageSearch(key: String, page: String): Flow<LatestReposUiState> =
        flow<LatestReposUiState> {
            val searchResult = httpsAPI.doPageSearch(key, page)
            if (searchResult.body()!!.items.isNotEmpty()) {
                emit(LatestReposUiState.Success(searchResult.body()!!.items))
            } else {
                throw HttpException(searchResult)
            }
        }.catch { it: Throwable ->
            emit(LatestReposUiState.Error(it))
        }.onStart {
            emit(LatestReposUiState.Loading)
        }
}