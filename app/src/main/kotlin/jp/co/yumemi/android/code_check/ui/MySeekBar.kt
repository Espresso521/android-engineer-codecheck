package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.SeekBar

class MySeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.seekBarStyle
) : SeekBar(context, attrs, defStyleAttr) {

    init {
        // 隐藏Thumb
        thumb = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 如果需要，可以在这里添加额外的绘制逻辑
    }
}