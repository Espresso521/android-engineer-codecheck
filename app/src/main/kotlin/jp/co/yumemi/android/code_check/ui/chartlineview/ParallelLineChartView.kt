package jp.co.yumemi.android.code_check.ui.chartlineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import jp.co.yumemi.android.code_check.ui.ChartLinePreviewBase

class ParallelLineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    // 虚线画笔
    private val dashedPaint = Paint().apply {
        color = lineColor
        strokeWidth = 3f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f) // 设置虚线效果
    }

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 平行线的间距
        val offset = 200f

        // 第一条实线，从左下角到右上角
        canvas.drawLine(0f, viewHeight, viewWidth, 0f, linePaint)

        // 第二条实线，平行于第一条线（向上偏移）
        canvas.drawLine(0f, viewHeight - offset, viewWidth, -offset, linePaint)

        // 中间的虚线，平行于第一条线
        canvas.drawLine(0f, viewHeight - offset / 2, viewWidth, -offset / 2, dashedPaint)
    }

    // 改变三条线的颜色
    override fun changeColor(newColor: Int) {
        linePaint.color = newColor
        dashedPaint.color = newColor
        invalidate()  // 触发重绘，应用颜色变化
    }
}