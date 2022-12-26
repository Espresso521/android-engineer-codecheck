package jp.co.yumemi.android.code_check.data

import android.util.Log
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.MainActivity
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchResultRepository @Inject constructor() {

    private val resultList = mutableListOf<RepoInfo>()
    var lastTimeKeyWord = ""

    private var INSTANCE: HttpClient? = client()

    val LAST_NULL = "LAST_NULL"
    private val nullRepoInfo = RepoInfo(
        name = LAST_NULL,
        fullName = LAST_NULL,
        language = LAST_NULL,
        description = LAST_NULL,
        stargazersCount = 0,
        watchersCount = 0,
        forksCount = 0,
        openIssuesCount = 0,
        createdTime = LAST_NULL,
        owner = RepoOwner(LAST_NULL, LAST_NULL)
    )

    fun client(): HttpClient {
        return INSTANCE ?: synchronized(this) {
            val newInstance = HttpClient(Android) {
                install(ResponseObserver) {
                    /**
                     *  status  description
                     *   200     OK
                     *   304     Not modified
                     *   422     Validation failed, or the endpoint has been spammed.
                     *   503     Service unavailable
                     */
                    onResponse { response ->
                        Log.d("SearchViewModel", "HTTP status: ${response.status.value}")
                    }
                }
            }
            INSTANCE = newInstance
            newInstance
        }
    }

    fun close() {
        Log.d("SearchResultRepository", "closing the client...")
        INSTANCE?.close()
        INSTANCE = null
    }

    suspend fun doSearch(keyWord: String): Result<List<RepoInfo>> = runCatching {
        if (lastTimeKeyWord == keyWord) {
            // the same keyWord, just return resultList
            return@runCatching resultList
        } else {
            lastTimeKeyWord = keyWord
            // if keyWord change, clear the resultList
            resultList.clear()
        }

        with(client()) {
            val response: HttpResponse = get("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", keyWord)
            }

            parseSearchResultJson(response)
            MainActivity.lastSearchDate = Date()
            return@runCatching resultList
        }
    }.onFailure {
        Log.e("SearchResultRepository", "Exception: ${it.stackTraceToString()}")
    }

    suspend fun doPageSearch(): Result<List<RepoInfo>> = runCatching {
        with(client()) {
            val response: HttpResponse = get("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", lastTimeKeyWord)
                parameter("page", (resultList.size / 30) + 1)
            }

            parseSearchResultJson(response)
            return@runCatching resultList
        }
    }.onFailure {
        // TODO : invalid: 422 Unprocessable Entity. Text: "{"message":"Only the first 1000 search results are available","documentation_url":"https://docs.github.com/v3/search/"}"
        Log.e("SearchResultRepository", "Exception: ${it.stackTraceToString()}")
    }


    private suspend fun parseSearchResultJson(response: HttpResponse) {
        if (resultList.size > 0 && resultList.last()?.fullName == "LAST_NULL") resultList.removeLast()

        val searchResult = Gson().fromJson(response.body<String>(), SearchResult::class.java)
        if(searchResult.items.isNotEmpty()) {
            resultList.addAll(searchResult.items)
            resultList.add(nullRepoInfo)
        }
    }

    //https://raw.githubusercontent.com/singgel/JAVA/master/README.md
    suspend fun getReadMe(fullName: String): Result<String> = kotlin.runCatching {
        with(client()) {
            val response: HttpResponse =
                get("https://raw.githubusercontent.com/$fullName/master/README.md") {
                    header("Accept", "application/vnd.github.v3+json")
                }

            response.body<String>().let {
                return@runCatching it
            }
        }
    }.onFailure {
        Log.e("SearchResultRepository", "Exception: ${it.stackTraceToString()}")
    }
}