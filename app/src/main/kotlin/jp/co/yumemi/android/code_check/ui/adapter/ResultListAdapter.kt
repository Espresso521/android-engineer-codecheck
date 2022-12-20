package jp.co.yumemi.android.code_check.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.databinding.RepoInfoViewBinding

class ResultListAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<RepoInfo, ResultListAdapter.RepoInfoViewHolder>(RepoInfoDiffCB()) {

    class RepoInfoViewHolder(private val binding: RepoInfoViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RepoInfo, viewLifecycleOwner: LifecycleOwner) {
            binding.run {
                lifecycleOwner = viewLifecycleOwner
                repoInfo = item
                executePendingBindings()
            }
        }
    }

    interface OnItemClickListener {
        fun itemClick(RepoInfo: RepoInfo)
    }

    override fun onBindViewHolder(holder: RepoInfoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewLifecycleOwner)
        holder.itemView.setOnClickListener {
            itemClickListener.itemClick(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoInfoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RepoInfoViewHolder(RepoInfoViewBinding.inflate(layoutInflater, parent, false))
    }

}