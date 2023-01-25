/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.data.ISearchRepository
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [jp.co.yumemi.android.code_check.ui.SearchFragment]
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    var searchResultRepository: ISearchRepository
) :
    ViewModel() {

    private var _searchResults: MutableLiveData<List<RepoInfo>> = MutableLiveData(listOf())
    val searchResults: LiveData<List<RepoInfo>> = _searchResults

    private var _selectedResults: MutableLiveData<RepoInfo> = MutableLiveData()
    val selectedResults: LiveData<RepoInfo> = _selectedResults

    private var _readMe: MutableLiveData<String> = MutableLiveData()
    val readMe: LiveData<String> = _readMe

    fun doSearch(keyWord: String) {
        viewModelScope.launch(ioDispatcher) {
            searchResultRepository.doSearch(keyWord).let { result ->
                result.fold(
                    onSuccess = {
                        _searchResults.postValue(it)
                    },
                    onFailure = {
                        it.printStackTrace()
                    }
                )
            }
        }
    }

    fun doPageSearch() {
        viewModelScope.launch(ioDispatcher) {
            searchResultRepository.doPageSearch().let { result ->
                result.fold(
                    onSuccess = {
                        _searchResults.postValue(it)
                    },
                    onFailure = {
                        it.printStackTrace()
                    }
                )
            }
        }
    }

    fun setSelectedItem(repoInfo: RepoInfo) {
        if (_selectedResults.value?.fullName != repoInfo.fullName) {
            _selectedResults.value = repoInfo
            getReadMe()
        }
    }

    private fun getReadMe() {
        selectedResults.value?.fullName?.let { fullName ->
            viewModelScope.launch((ioDispatcher)) {
                searchResultRepository.getReadMe(fullName).let { result ->
                    result.fold(
                        onSuccess = {
                            _readMe.postValue(it)
                        },
                        onFailure = {
                            _readMe.postValue("No ReadMe.md")
                        }
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchResultRepository.close()
    }

}
