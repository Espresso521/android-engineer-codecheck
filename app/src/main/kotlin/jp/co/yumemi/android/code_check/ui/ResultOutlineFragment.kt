/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui

import android.view.View
import android.widget.ScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import jp.co.yumemi.android.code_check.databinding.FragmentResultBinding
import jp.co.yumemi.android.code_check.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ResultOutlineFragment : BaseFragment<FragmentResultBinding>(FragmentResultBinding::inflate) {

    private val searchViewModel: SearchViewModel by activityViewModels()

    @Inject
    lateinit var markdown: Markwon

    override fun setUpViewsAndObserve(view: View) {
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
    }
}
