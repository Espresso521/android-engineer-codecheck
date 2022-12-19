/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.FragmentSearchBinding
import jp.co.yumemi.android.code_check.model.RepoInfo
import jp.co.yumemi.android.code_check.ui.adapter.ResultListAdapter
import jp.co.yumemi.android.code_check.viewmodel.SearchViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var adapter: ResultListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = ResultListAdapter(object : ResultListAdapter.OnItemClickListener {
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
                    searchViewModel.searchResults(keyword).let { ret ->
                        hideSoftInput(view)
                        if(!ret) toast("No result. Try again")
                    }
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
        val directions =
            SearchFragmentDirections.actionSearchFragmentToResultOutlineFragment(repoInfo = repoInfo)
        findNavController().navigate(directions)
    }

    private fun hideSoftInput(view: View) {
        (view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }

    private fun toast(content: String) {
        Toast.makeText(
            context,
            content,
            Toast.LENGTH_SHORT
        ).show()
    }
}


