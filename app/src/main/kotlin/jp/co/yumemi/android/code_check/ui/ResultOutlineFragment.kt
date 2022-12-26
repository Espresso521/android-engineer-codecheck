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
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.FragmentResultBinding
import jp.co.yumemi.android.code_check.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ResultOutlineFragment : Fragment(R.layout.fragment_result) {

    private var _binding: FragmentResultBinding? = null

    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var markdown: Markwon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        // obtain an instance of Markwon
        markdown = context?.let { Markwon.create(it.applicationContext) }!!

        searchViewModel.selectedResults.observe(viewLifecycleOwner) {
            binding.repoInfo = it
            binding.resultScrollView.fullScroll(ScrollView.FOCUS_UP)
        }

        searchViewModel.readMe.observe(viewLifecycleOwner) {
            it?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    var toMarkDown = markdown.toMarkdown(it)
                    withContext(Dispatchers.Main) {
                        markdown.setParsedMarkdown(binding.readme, toMarkDown)
                    }
                }

            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
