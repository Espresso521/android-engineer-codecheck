package jp.co.yumemi.android.code_check.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import jp.co.yumemi.android.code_check.data.RepoInfo

class RepoInfoDiffCB : DiffUtil.ItemCallback<RepoInfo>() {
    override fun areItemsTheSame(oldItem: RepoInfo, newItem: RepoInfo): Boolean {
        return oldItem.fullName == newItem.fullName
    }

    override fun areContentsTheSame(oldItem: RepoInfo, newItem: RepoInfo): Boolean {
        return oldItem.fullName == newItem.fullName
    }
}
