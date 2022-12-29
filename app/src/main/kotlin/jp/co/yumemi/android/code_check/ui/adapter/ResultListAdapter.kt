package jp.co.yumemi.android.code_check.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.data.RepoInfo
import jp.co.yumemi.android.code_check.databinding.RepoInfoViewBinding
import jp.co.yumemi.android.code_check.databinding.RepoLoadingBinding
import javax.inject.Inject

class ResultListAdapter @Inject constructor(
    private val fragment: Fragment,
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

    lateinit var onItemClickListener : OnItemClickListener

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
        holder.bind(item, fragment.viewLifecycleOwner)
        holder.itemView.setOnClickListener {
            onItemClickListener.itemClick(item)
        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {}


}