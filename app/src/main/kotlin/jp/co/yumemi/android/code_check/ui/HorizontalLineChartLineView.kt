package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class HorizontalLineChartLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 在视图的垂直中心位置画一条横线
        val yPosition = viewHeight / 2

        // 使用 linePaint 绘制横线
        canvas.drawLine(0f, yPosition, viewWidth, yPosition, linePaint)
    }
}