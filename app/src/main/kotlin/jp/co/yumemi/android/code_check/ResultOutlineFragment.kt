/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.MainActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.FragmentResultBinding

class ResultOutlineFragment : Fragment(R.layout.fragment_result) {

    private val args: ResultOutlineFragmentArgs by navArgs()

    private var _binding: FragmentResultBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        var repoInfo = args.repoInfo

        binding.ownerIconView.load(repoInfo.ownerIconUrl);
        binding.nameView.text = repoInfo.name;
        binding.languageView.text = context?.getString(R.string.written_language, repoInfo.language)
        binding.starsView.text = "${repoInfo.stargazersCount} stars";
        binding.watchersView.text = "${repoInfo.watchersCount} watchers";
        binding.forksView.text = "${repoInfo.forksCount} forks";
        binding.openIssuesView.text = "${repoInfo.openIssuesCount} open issues";
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
