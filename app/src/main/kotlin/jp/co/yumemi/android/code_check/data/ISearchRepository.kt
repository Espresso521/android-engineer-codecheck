package jp.co.yumemi.android.code_check.data

interface ISearchRepository {
    suspend fun doSearch(keyWord: String): Result<List<RepoInfo>>
    suspend fun doPageSearch(): Result<List<RepoInfo>>
    suspend fun getReadMe(fullName: String): Result<String>
    fun close()
}