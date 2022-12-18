/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.FragmentResultBinding

class ResultOutlineFragment : Fragment(R.layout.fragment_result) {

    private val args: ResultOutlineFragmentArgs by navArgs()

    private var binding: FragmentResultBinding? = null
    private val _binding get() = binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        binding = FragmentResultBinding.bind(view)

        var repoInfo = args.repoInfo

        _binding.ownerIconView.load(repoInfo.ownerIconUrl);
        _binding.nameView.text = repoInfo.name;
        _binding.languageView.text = repoInfo.language;
        _binding.starsView.text = "${repoInfo.stargazersCount} stars";
        _binding.watchersView.text = "${repoInfo.watchersCount} watchers";
        _binding.forksView.text = "${repoInfo.forksCount} forks";
        _binding.openIssuesView.text = "${repoInfo.openIssuesCount} open issues";
    }
}
