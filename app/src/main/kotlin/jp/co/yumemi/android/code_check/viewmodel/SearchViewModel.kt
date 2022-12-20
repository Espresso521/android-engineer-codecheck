/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

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

}
