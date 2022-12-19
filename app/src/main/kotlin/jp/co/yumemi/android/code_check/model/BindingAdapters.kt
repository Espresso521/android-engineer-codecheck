package jp.co.yumemi.android.code_check.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object BindingAdapters {
    @BindingAdapter("avatar")
    @JvmStatic
    fun loadImage(view: ImageView, url:String){
        view.load(url);
    }
}