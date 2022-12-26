package jp.co.yumemi.android.code_check.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.noties.markwon.Markwon
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object MarkDownModule {

    @Provides
    @Singleton
    fun provideMarkdown(@ApplicationContext appContext: Context): Markwon {
        return Markwon.create(appContext);
    }
}