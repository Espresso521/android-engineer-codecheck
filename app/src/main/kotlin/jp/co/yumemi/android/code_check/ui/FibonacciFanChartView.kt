package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet

class FibonacciFanChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    // 百分比标注，按照从最近到最远的顺序
    private val fibonacciLevels = listOf(38.2f, 50.0f, 61.8f)

    // 基准线的虚线画笔
    private val dashedPaint = Paint().apply {
        color = lineColor
        strokeWidth = 5f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)  // 虚线效果
    }

    // 文字画笔，用于标注百分比
    private val textPaint = Paint().apply {
        color = lineColor
        textSize = 40f
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER  // 设置文字居中
    }

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 起点坐标，斐波那契扇形从此点向右上发散
        val startX = viewWidth * 0.1f
        val startY = viewHeight * 0.9f

        // 基准线（虚线）
        canvas.drawLine(startX, startY, viewWidth , viewHeight * 0.1f, dashedPaint)

        // 绘制斐波那契线，从最近的开始画
        for ((index, level) in fibonacciLevels.withIndex()) {
            // 根据斐波那契比例计算终点坐标
            val endX = viewWidth
            val endY = (startY * (level / 100f))

            // 画实线
            canvas.drawLine(startX, startY, endX, endY, linePaint)

            // 在斐波那契线中间位置标注百分比，居中显示
            val midX = (startX + endX) / 2
            val midY = (startY + endY) / 2
            canvas.drawText("${fibonacciLevels[index]}%", midX, midY - 10f, textPaint)
        }
    }

    // 改变所有线的颜色
    override fun changeColor(newColor: Int) {
        linePaint.color = newColor
        dashedPaint.color = newColor
        textPaint.color = newColor
        invalidate()  // 触发重绘
    }
}