package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet

class RectangleChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 定义长方形的边界
        val rect = RectF(
            viewWidth * 0.2f,  // 左边界
            viewHeight * 0.3f, // 上边界
            viewWidth * 0.8f,  // 右边界
            viewHeight * 0.7f  // 下边界
        )

        // 使用linePaint绘制长方形
        canvas.drawRect(rect, linePaint)
    }
}