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
import jp.co.yumemi.android.code_check.data.RepositoryRemoteDataSource
import jp.co.yumemi.android.code_check.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
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


    fun doSearch(keyWord: String) {

    }
}
