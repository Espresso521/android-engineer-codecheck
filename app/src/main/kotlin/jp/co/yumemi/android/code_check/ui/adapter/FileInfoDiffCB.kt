package jp.co.yumemi.android.code_check.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import jp.co.yumemi.android.code_check.data.Mp4FileMetadata

class FileInfoDiffCB : DiffUtil.ItemCallback<Mp4FileMetadata>() {
    override fun areItemsTheSame(oldItem: Mp4FileMetadata, newItem: Mp4FileMetadata): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: Mp4FileMetadata, newItem: Mp4FileMetadata): Boolean {
        return oldItem.path == newItem.path
    }
}
