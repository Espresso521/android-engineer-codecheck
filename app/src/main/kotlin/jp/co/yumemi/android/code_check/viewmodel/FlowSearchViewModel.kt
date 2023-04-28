/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

@HiltViewModel
class FlowSearchViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private var searchResultRepository: RepositoryRemoteDataSource
) :
    ViewModel() {

    private val resultList = mutableListOf<RepoInfo>()
    private var lastKeyWord: String = ""

    private var _selectedResults: MutableLiveData<RepoInfo> = MutableLiveData()
    val selectedResults: LiveData<RepoInfo> = _selectedResults

    private var _readMe: MutableLiveData<String> = MutableLiveData()
    val readMe: LiveData<String> = _readMe

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow<LatestReposUiState>(LatestReposUiState.Success(emptyList()))
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<LatestReposUiState> = _uiState

    fun doSearch(keyWord: String) = viewModelScope.launch(ioDispatcher) {
        if(lastKeyWord == keyWord && resultList.size != 0) {
            _uiState.value = LatestReposUiState.Success(resultList)
        } else {
            lastKeyWord = keyWord
            searchResultRepository.getSearchResult(keyWord).collect { searchResult ->
                when(searchResult) {
                    is LatestReposUiState.Success -> {
                        resultList.addAll(searchResult.repos)
                        _uiState.value = LatestReposUiState.Success(resultList)
                    }
                    is LatestReposUiState.Error -> {
                        _uiState.value = LatestReposUiState.Error(searchResult.exception)
                    }
                    is LatestReposUiState.Loading -> {
                        _uiState.value = LatestReposUiState.Loading
                    }
                }
            }
        }
    }

    fun doPageSearch(key: String, page: String) = viewModelScope.launch(ioDispatcher) {
        searchResultRepository.doPageSearch(key, page).collect { searchResult ->
            when(searchResult) {
                is LatestReposUiState.Success -> {
                    resultList.addAll(searchResult.repos)
                    _uiState.value = LatestReposUiState.Success(resultList)
                }
                is LatestReposUiState.Error -> {
                    _uiState.value = LatestReposUiState.Error(searchResult.exception)
                }
                is LatestReposUiState.Loading -> {
                    _uiState.value = LatestReposUiState.Loading
                }
            }
        }
    }

    fun getListCount(): Int {
        return resultList.size
    }

    fun setSelectedItem(repoInfo: RepoInfo) {
        if (_selectedResults.value?.fullName != repoInfo.fullName) {
            _selectedResults.value = repoInfo
            getReadMe()
            _readMe.postValue("Loading...")
        }
    }

    private fun getReadMe() {
        selectedResults.value?.fullName?.let { fullName ->
            viewModelScope.launch((ioDispatcher)) {
                val url = "https://raw.githubusercontent.com/$fullName/master/README.md"
                searchResultRepository.getReadMe(url).let { result ->
                    if(result.isEmpty()) _readMe.postValue("No ReadMe.md")
                    else _readMe.postValue(result)
                }
            }
        }
    }
}

sealed class LatestReposUiState {
    data class Success(val repos: List<RepoInfo>): LatestReposUiState()
    data class Error(val exception: Throwable): LatestReposUiState()
    object Loading: LatestReposUiState()
}
