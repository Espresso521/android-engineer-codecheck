package jp.co.yumemi.android.code_check.ui.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import jp.co.yumemi.android.code_check.R

object BindingAdapters {
    @BindingAdapter("avatar")
    @JvmStatic
    fun loadImage(view: ImageView, url:String?){
        url?.let { view.load(it) } ?: view.load(R.mipmap.github_icon)
    }
}