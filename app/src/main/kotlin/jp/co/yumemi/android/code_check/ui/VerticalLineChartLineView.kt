package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class VerticalLineChartLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 在视图的水平中心位置画一条竖线
        val xPosition = viewWidth / 2

        // 使用 linePaint 绘制竖线
        canvas.drawLine(xPosition, 0f, xPosition, viewHeight, linePaint)
    }
}