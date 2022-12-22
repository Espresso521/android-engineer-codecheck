/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.noties.markwon.Markwon
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.FragmentResultBinding
import jp.co.yumemi.android.code_check.viewmodel.SearchViewModel

class ResultOutlineFragment : Fragment(R.layout.fragment_result) {

    private var _binding: FragmentResultBinding? = null

    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var markdown: Markwon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        searchViewModel.selectedResults.observe(viewLifecycleOwner) {
            binding.repoInfo = it
            binding.resultScrollView.fullScroll(ScrollView.FOCUS_UP)
        }

        searchViewModel.readMe.observe(viewLifecycleOwner) {
            it?.let { markdown.setMarkdown(binding.readme, it) }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // obtain an instance of Markwon
        markdown = context?.let { Markwon.create(it.applicationContext) }!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
