/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.databinding.FragmentSearchBinding
import jp.co.yumemi.android.code_check.ui.adapter.ResultListAdapter
import jp.co.yumemi.android.code_check.viewmodel.SearchViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var adapter: ResultListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = ResultListAdapter(viewLifecycleOwner, object : ResultListAdapter.OnItemClickListener {
            override fun itemClick(RepoInfo: RepoInfo) {
                gotoRepositoryFragment(RepoInfo)
            }
        })

        searchViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            adapter.submitList(searchResults)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                editText.text.toString().let { keyword ->
                    if (keyword.isNotEmpty()) {
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
            it.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
            it.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun gotoRepositoryFragment(repoInfo: RepoInfo) {
        searchViewModel.setSelectedItem(repoInfo)
        if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToResultOutlineFragment(
                    repoInfo = repoInfo
                )
            )
        }
    }

    private fun hideSoftInput(view: View) {
        (view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }

}


