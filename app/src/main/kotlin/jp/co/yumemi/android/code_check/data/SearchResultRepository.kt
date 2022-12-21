package jp.co.yumemi.android.code_check.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.MainActivity
import org.json.JSONObject
import java.util.*

class SearchResultRepository {

    companion object {
        fun getInstance() = Helper.instance
    }

    private object Helper {
        val instance = SearchResultRepository()
    }

    private val client = HttpClient(Android) {
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

    suspend fun doSearch(keyWord: String): Result<List<RepoInfo>> = runCatching {
        val resultList = mutableListOf<RepoInfo>()

        val response: HttpResponse = client.get("https://api.github.com/search/repositories") {
            header("Accept", "application/vnd.github.v3+json")
            parameter("q", keyWord)
        }

        JSONObject(response.receive<String>()).let { jsonObject ->
            jsonObject.optJSONArray("items")?.let { JSONArray ->
                for (i in 0 until JSONArray.length()) {
                    JSONArray.optJSONObject(i)?.let { jsonItem ->
                        val fullName = jsonItem.optString("full_name")
                        val name = jsonItem.optString("name")
                        val ownerIconUrl =
                            jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
                        val loginName = jsonItem.optJSONObject("owner")?.optString("login") ?: ""
                        val language = jsonItem.optString("language")
                        val stargazersCount = jsonItem.optLong("stargazers_count")
                        val watchersCount = jsonItem.optLong("watchers_count")
                        val forksCount = jsonItem.optLong("forks")
                        val openIssuesCount = jsonItem.optLong("open_issues_count")
                        val description = jsonItem.optString("description")
                        val apiUrl = jsonItem.optString("url")

                        resultList.add(
                            RepoInfo(
                                name = name,
                                fullName = fullName,
                                loginName = loginName,
                                ownerIconUrl = ownerIconUrl,
                                language = language,
                                description = description,
                                stargazersCount = stargazersCount,
                                watchersCount = watchersCount,
                                forksCount = forksCount,
                                openIssuesCount = openIssuesCount
                            )
                        )
                    }
                }
            }
        }
        MainActivity.lastSearchDate = Date()
        return@runCatching resultList
    }.onFailure {
        Log.e("SearchResultRepository", "Exception: ${it.stackTraceToString()}")
    }

    //https://raw.githubusercontent.com/singgel/JAVA/master/README.md
    suspend fun getReadMe(fullName: String): Result<String> = kotlin.runCatching {
        val response: HttpResponse = client.get("https://raw.githubusercontent.com/" + fullName + "/master/README.md") {
            header("Accept", "application/vnd.github.v3+json")
        }

        response.receive<String>().let {
            return@runCatching it
        }
    }.onFailure {
        Log.e("SearchResultRepository", "Exception: ${it.stackTraceToString()}")
    }
}