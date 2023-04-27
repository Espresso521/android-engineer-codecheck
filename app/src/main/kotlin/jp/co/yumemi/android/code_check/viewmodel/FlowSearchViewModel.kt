/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.data.RepositoryRemoteDataSource
import jp.co.yumemi.android.code_check.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [jp.co.yumemi.android.code_check.ui.SearchFragment]
 */
@HiltViewModel
class FlowSearchViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    var searchResultRepository: RepositoryRemoteDataSource
) :
    ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(LatestReposUiState.Success(emptyList()))
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<LatestReposUiState> = _uiState

    fun doSearch(keyWord: String) = viewModelScope.launch(ioDispatcher) {
        searchResultRepository.getSearchResult(keyWord).collect { searchResult ->
            if (searchResult.items.isNotEmpty()) {
                _uiState.value = LatestReposUiState.Success(searchResult.items)
            }
        }
    }
}

sealed class LatestReposUiState {
    data class Success(val repos: List<RepoInfo>): LatestReposUiState()
    data class Error(val exception: Throwable): LatestReposUiState()
}
