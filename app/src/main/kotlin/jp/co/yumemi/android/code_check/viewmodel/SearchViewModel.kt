/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.data.SearchResultRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for [jp.co.yumemi.android.code_check.ui.SearchFragment]
 */
class SearchViewModel : ViewModel() {

    private val searchResultRepository: SearchResultRepository =
        SearchResultRepository.getInstance()

    private var _searchResults: MutableLiveData<List<RepoInfo>> = MutableLiveData(listOf())
    val searchResults: LiveData<List<RepoInfo>> = _searchResults

    private var _selectedResults: MutableLiveData<RepoInfo> = MutableLiveData()
    val selectedResults: LiveData<RepoInfo> = _selectedResults

    private var _readMe: MutableLiveData<String> = MutableLiveData();
    val readMe: LiveData<String> = _readMe

    fun doSearch(keyWord: String) {
        viewModelScope.launch {
            searchResultRepository.doSearch(keyWord).let { result ->
                result.fold(
                    onSuccess = {
                        _searchResults.value = it
                    },
                    onFailure = {
                        it.printStackTrace()
                    }
                )
            }
        }
    }

    fun setSelectedItem(repoInfo: RepoInfo) {
        _selectedResults.value = repoInfo
    }

    fun getReadMe() {
        selectedResults.value?.fullName?.let { fullName ->
            viewModelScope.launch {
                searchResultRepository.getReadMe(fullName).let { result ->
                    result.fold(
                        onSuccess = {
                            Log.e("SearchViewModel", "getReadMe onSuccess: $it")
                            _readMe.value = it
                        },
                        onFailure = {
                            it.printStackTrace()
                        }
                    )
                }
            }
        }
    }

}
