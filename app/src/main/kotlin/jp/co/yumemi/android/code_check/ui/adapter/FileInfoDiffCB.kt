package jp.co.yumemi.android.code_check.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import java.io.File

class FileInfoDiffCB : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path
    }
}
