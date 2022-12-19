/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.util.Log
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.MainActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.model.RepoInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.*

/**
 * Do repositories search option for [SearchFragment]
 */
class SearchViewModel : ViewModel() {

    /**
     * Search repositories for keyWord, return list of [RepoInfo]
     */
    fun searchResults(keyWord: String): List<RepoInfo> = runBlocking {
        val client = HttpClient(Android) {
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

        return@runBlocking GlobalScope.async {
            var response: HttpResponse
            val resultList = mutableListOf<RepoInfo>()
            try {
                response = client.get("https://api.github.com/search/repositories") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("q", keyWord)
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", e.toString())
                return@async resultList.toList()
            } finally {
                client.close()
            }

            JSONObject(response.receive<String>()).let { jsonObject ->
                jsonObject.optJSONArray("items")?.let { JSONArray ->
                    for (i in 0 until JSONArray.length()) {
                        JSONArray.optJSONObject(i)?.let { jsonItem ->
                            val name = jsonItem.optString("full_name")
                            val ownerIconUrl =
                                jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
                            val language = jsonItem.optString("language")
                            val stargazersCount = jsonItem.optLong("stargazers_count")
                            val watchersCount = jsonItem.optLong("watchers_count")
                            val forksCount = jsonItem.optLong("forks")
                            val openIssuesCount = jsonItem.optLong("open_issues_count")

                            resultList.add(
                                RepoInfo(
                                    name = name,
                                    ownerIconUrl = ownerIconUrl,
                                    language = language,
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

            lastSearchDate = Date()

            return@async resultList.toList()
        }.await()
    }
}
