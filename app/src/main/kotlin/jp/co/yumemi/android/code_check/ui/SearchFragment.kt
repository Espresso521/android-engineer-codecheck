/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.databinding.FragmentSearchBinding
import jp.co.yumemi.android.code_check.ui.adapter.ResultListAdapter
import jp.co.yumemi.android.code_check.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var adapter: ResultListAdapter

    private var isLoading = false

    private var lastKeyWord = ""

    override fun setUpViewsAndObserve(view: View) {
        adapter =
            ResultListAdapter(viewLifecycleOwner, object : ResultListAdapter.OnItemClickListener {
                override fun itemClick(RepoInfo: RepoInfo) {
                    gotoRepositoryFragment(RepoInfo)
                }
            })

        searchViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            adapter.submitList(searchResults.toList())
        }

        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                editText.text.toString().let { keyword ->
                    if (keyword.isNotEmpty() && lastKeyWord != keyword) {
                        lastKeyWord = keyword
                        searchViewModel.doSearch(keyword)
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
                            && linearLayoutManager.findLastCompletelyVisibleItemPosition() == searchViewModel.searchResults.value?.size?.minus(
                                1
                            )
                        ) {
                            isLoading = true
                            loadMore()
                        }
                    }
                }
            })
        }
    }

    fun loadMore() {
        lifecycleScope.launch(Dispatchers.IO) {
            searchViewModel.doPageSearch()
            isLoading = false
        }
    }

    fun gotoRepositoryFragment(repoInfo: RepoInfo) {
        searchViewModel.setSelectedItem(repoInfo)
        if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToResultOutlineFragment()
            )
        }
    }

}


