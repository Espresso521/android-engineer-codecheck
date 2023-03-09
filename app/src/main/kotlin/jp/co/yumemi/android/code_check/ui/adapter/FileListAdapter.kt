package jp.co.yumemi.android.code_check.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.data.Mp4FileMetadata
import jp.co.yumemi.android.code_check.databinding.FileInfoViewBinding
import javax.inject.Inject

class FileListAdapter @Inject constructor(
    private val fragment: Activity,
) : ListAdapter<Mp4FileMetadata, RecyclerView.ViewHolder>(FileInfoDiffCB()) {

    class FileInfoViewHolder(private val binding: FileInfoViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Mp4FileMetadata, viewLifecycleOwner: LifecycleOwner) {
            binding.run {
                lifecycleOwner = viewLifecycleOwner
                fileInfo = item
                executePendingBindings()
            }
        }
    }

    lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun itemClick(fileMetadata: Mp4FileMetadata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FileInfoViewHolder(FileInfoViewBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FileInfoViewHolder) {
            showFileInfoView(holder, position)
        }
    }

    private fun showFileInfoView(holder: FileInfoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, fragment as LifecycleOwner)
        holder.itemView.setOnClickListener {
            onItemClickListener.itemClick(item)
        }
    }
}
