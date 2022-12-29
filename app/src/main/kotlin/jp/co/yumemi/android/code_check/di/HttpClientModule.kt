package jp.co.yumemi.android.code_check.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.observer.*
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object HttpClientModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
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

        return newInstance
    }
}