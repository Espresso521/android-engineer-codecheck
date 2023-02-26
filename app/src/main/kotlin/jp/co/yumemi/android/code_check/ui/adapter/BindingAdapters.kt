package jp.co.yumemi.android.code_check.ui.adapter

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide
import jp.co.yumemi.android.code_check.R

object BindingAdapters {
    @BindingAdapter("avatar")
    @JvmStatic
    fun loadImage(view: ImageView, url:String?){
        url?.let { view.load(it) } ?: view.load(R.mipmap.github_icon)
    }

    @BindingAdapter("bitmap")
    @JvmStatic
    fun loadBitMap(view: ImageView, bitmap: Bitmap?){
        Glide.with(view.context).load(bitmap).into(view)
//        bitmap?.let {
//            view.load(it)
//        }
    }
}