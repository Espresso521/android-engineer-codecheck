package jp.co.yumemi.android.code_check.ui.chartlineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import jp.co.yumemi.android.code_check.ui.ChartLinePreviewBase

class FibonacciHorizontalLinesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    // 百分比标注
    private val fibonacciLevels = listOf(0.0f, 23.6f, 38.2f, 50.0f, 61.8f, 100.0f)

    // 画标注的文字画笔
    private val textPaint = Paint().apply {
        color = lineColor
        textSize = 40f
        style = Paint.Style.FILL
        textAlign = Paint.Align.RIGHT
    }

    // 上下的padding，用来避免文字被遮挡
    private val verticalPadding = 200f

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val drawableHeight = viewHeight - 2 * verticalPadding // 去掉上下padding后的高度

        // 绘制每条斐波那契线及对应标注，从上到下绘制
        for (level in fibonacciLevels) {
            // 根据百分比计算线的位置（从上到下，并留有padding）
            val yPosition = verticalPadding + drawableHeight * (level / 100f)

            // 画斐波那契线
            canvas.drawLine(0f, yPosition, viewWidth, yPosition, linePaint)

            // 在右边标注百分比文字
            canvas.drawText("${level}%", viewWidth - 15f, yPosition - 10f, textPaint)
        }
    }

    // 改变所有线和文字的颜色
    override fun changeColor(newColor: Int) {
        linePaint.color = newColor
        textPaint.color = newColor
        invalidate()  // 触发重绘
    }
}