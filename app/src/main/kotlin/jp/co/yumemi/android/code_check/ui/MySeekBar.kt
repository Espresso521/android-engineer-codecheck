package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar

class MySeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.seekBarStyle
) : SeekBar(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener {

    interface onStopTrackingTouchListener {
        fun onStopTrackingTouch(seekBar: SeekBar)
    }

    private var mOnStopTrackingTouchListener: onStopTrackingTouchListener? = null

    fun setOnStopTrackingTouchListener(stopTrackingTouchListener: onStopTrackingTouchListener) {
        this.mOnStopTrackingTouchListener = stopTrackingTouchListener
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mOnStopTrackingTouchListener?.let { listener ->
            seekBar?.let { seek ->
                listener.onStopTrackingTouch(seekBar)
            }
        }
    }


}