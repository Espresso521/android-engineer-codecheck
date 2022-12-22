package jp.co.yumemi.android.code_check.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.spyk
import jp.co.yumemi.android.code_check.data.creators.SearchErrorJSON
import jp.co.yumemi.android.code_check.data.creators.SearchSuccessJSON
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchResultRepositoryTest {

    private val responseHeaders = headersOf(HttpHeaders.Accept, "application/vnd.github.v3+json")

    private val ktorSuccessClient: HttpClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                when (request.url.toString()) {
                    "https://api.github.com/search/repositories?q=java" -> respond(
                        SearchSuccessJSON,
                        HttpStatusCode.OK,
                        responseHeaders
                    )
                    "https://api.github.com/search/repositories?q=" -> respond(
                        SearchErrorJSON,
                        HttpStatusCode.UnprocessableEntity,
                        responseHeaders
                    )
                    else -> error("Unhandled ${request.url}")
                }
            }
        }
        expectSuccess = true
    }

    private val searchResultRepository = spyk<SearchResultRepository>()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun doSearchJava() = runTest {

        every { searchResultRepository.client() } returns ktorSuccessClient

        searchResultRepository.doSearch("java").let { result ->
            result.fold(
                onSuccess = {
                    assertEquals(it[0].loginName, "JetBrains")
                    assertEquals(it[1].loginName, "hussien89aa")
                },
                onFailure = {
                    assert(false)
                }
            )
        }
    }

    @Test
    fun doSearchEmpty() = runTest {

        every { searchResultRepository.client() } returns ktorSuccessClient

        searchResultRepository.doSearch("").let { result ->
            result.fold(
                onSuccess = {
                    assert(false)
                },
                onFailure = {
                    assert(true)
                }
            )
        }
    }

}