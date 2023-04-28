/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.databinding.FragmentSearchBinding
import jp.co.yumemi.android.code_check.ui.adapter.ResultListAdapter
import jp.co.yumemi.android.code_check.viewmodel.FlowSearchViewModel
import jp.co.yumemi.android.code_check.viewmodel.LatestReposUiState
import jp.co.yumemi.android.code_check.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val flowSearchViewModel: FlowSearchViewModel by activityViewModels()

    @Inject
    lateinit var adapter: ResultListAdapter

    @Inject
    lateinit var progressDismissDialog: ProgressDismissDialog

    private var isLoading = false

    private var lastKeyWord = ""

    override fun setUpViewsAndObserve(view: View) {
        adapter.onItemClickListener = object : ResultListAdapter.OnItemClickListener {
            override fun itemClick(RepoInfo: RepoInfo) {
                gotoRepositoryFragment(RepoInfo)
            }
        }

        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                editText.text.toString().let { keyword ->
                    if (keyword.isNotEmpty() && lastKeyWord != keyword) {
                        lastKeyWord = keyword
                        flowSearchViewModel.doSearch(keyword)
                    }
                    hideSoftInput(view)
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (!isLoading) {
                        if (linearLayoutManager != null
                            && linearLayoutManager.findLastCompletelyVisibleItemPosition() == flowSearchViewModel.getListCount()
                                .minus(
                                    1
                                )
                        ) {
                            isLoading = true
                            if (lastKeyWord.isNotEmpty()) {
                                loadMore(
                                    lastKeyWord,
                                    flowSearchViewModel.getListCount().div(30).plus(1).toString()
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * U must call follow function in onCreate()
         * if U call it in onCreateView(), the collect will be callback too many times!!!
         * */
        // Start a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                flowSearchViewModel.uiState.collect { uiState ->
                    // New value received
                    when (uiState) {
                        is LatestReposUiState.Loading -> {
                            progressDismissDialog.startLoadingDialog()
                        }
                        is LatestReposUiState.Success -> {
                            adapter.submitList(uiState.repos.toList())
                            isLoading = false
                            progressDismissDialog.dismissDialog()
                        }
                        is LatestReposUiState.Error -> {
                            isLoading = false
                            progressDismissDialog.dismissDialog()
                            Toast.makeText(activity, "$uiState.exception", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    fun loadMore(key: String, page: String) {
        flowSearchViewModel.doPageSearch(key, page)
    }

    fun gotoRepositoryFragment(repoInfo: RepoInfo) {
        flowSearchViewModel.setSelectedItem(repoInfo)
        if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToResultOutlineFragment()
            )
        }
    }

}


