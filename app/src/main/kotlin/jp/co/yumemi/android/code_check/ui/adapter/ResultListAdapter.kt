package jp.co.yumemi.android.code_check.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.databinding.RepoInfoViewBinding
import jp.co.yumemi.android.code_check.databinding.RepoLoadingBinding

class ResultListAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<RepoInfo, RecyclerView.ViewHolder>(RepoInfoDiffCB()) {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

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

    private class LoadingViewHolder(binding: RepoLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var progressBar  = binding.loadProgressBar
    }

    interface OnItemClickListener {
        fun itemClick(RepoInfo: RepoInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val layoutInflater = LayoutInflater.from(parent.context)
            return RepoInfoViewHolder(RepoInfoViewBinding.inflate(layoutInflater, parent, false))
        } else {
            val layoutInflater = LayoutInflater.from(parent.context)
            return LoadingViewHolder(RepoLoadingBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RepoInfoViewHolder) {
            showRepoInfoView(holder, position)
        } else if (holder is LoadingViewHolder) {
            showLoadingView(holder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).owner.ownerIconUrl == "LAST_NULL") VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    private fun showRepoInfoView(holder: RepoInfoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewLifecycleOwner)
        holder.itemView.setOnClickListener {
            itemClickListener.itemClick(item)
        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {}


}